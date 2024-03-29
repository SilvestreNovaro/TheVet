package com.example.veterinaria.service;

import com.example.veterinaria.DTO.CustomerDTO;
import com.example.veterinaria.DTO.CustomerWithNeuteredPetDTO;
import com.example.veterinaria.convert.UtilityService;
import com.example.veterinaria.entity.*;
import com.example.veterinaria.exception.ApiExceptionHandler;
import com.example.veterinaria.exception.BadRequestException;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.CustomerRepository;
import com.example.veterinaria.repository.PetRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.hibernate.tool.schema.spi.ExceptionHandler;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;


import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@AllArgsConstructor
@Service

public class CustomerService {

    @Autowired
    private final ModelMapper modelMapper;

    private final CustomerRepository customerRepository;

    private final PetService petService;

    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final PetRepository petRepository;

    private final SpringTemplateEngine templateEngine;

    private JavaMailSender javaMailSender;

    private ApiExceptionHandler apiExceptionHandler;

    private static final String sender = "noreply@vethome.com";

    private static final String NOT_FOUND_CUSTOMER = "Customer not found";

    private static final String NOT_FOUND_ROLE = "Role not found";


    @Autowired
    UtilityService utilityService;



    public void createCustomer(CustomerDTO customerDTO) throws MessagingException {
        customerRepository.findByEmail(customerDTO.getEmail()).ifPresent( c -> {
            throw new BadRequestException("Email already in use");
        });
        customerRepository.findByContactNumber(customerDTO.getContactNumber()).ifPresent( customer -> {
            throw new BadRequestException("Phone number already in use");
        });
        Customer customer = utilityService.convertCustomerDTOtoCustomerCreate(customerDTO);
        String encodedPassword = this.passwordEncoder.encode(customerDTO.getPassword());
        customer.setPassword(encodedPassword);
        Role role = roleService.findById(1L).orElseThrow(() -> new NotFoundException(NOT_FOUND_ROLE));
        customer.setRole(role);
        sendRegistrationEmail(customer);
        customerRepository.save(customer);
    }

    public void sendRegistrationEmail(Customer customer) throws MessagingException {
        String recipient = customer.getEmail();
        String subject = "Registry exitoso en VETHOME";

        Context context = new Context();
        context.setVariable("name", customer.getName());

        // Procesar la plantilla Thymeleaf con el Context
        String content = templateEngine.process("email-template", context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(recipient);
        helper.setSubject(subject);


        // Establece el contenido como HTML
        helper.setText(content, true);
        //ESTA LINEA ENVIA EL MAIL DE RECORDATORIO DE FORMA CORRECTA!!
        javaMailSender.send(mimeMessage);
    }

    public void sendEmailWhenLastMedicalRecordSixMonths(Customer customer, String petName, LocalDateTime date) throws MessagingException {

            String recipient = customer.getEmail();
            String subject = "Hora de llevar a tu mascota!";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDate = date.format(formatter);

            Context context = new Context();
            context.setVariable("name", customer.getName());
            context.setVariable("petName", petName);
            context.setVariable("recordDate", formattedDate);

            // Procesar la plantilla Thymeleaf con el Context
            String content = templateEngine.process("chek-pet-notification-template", context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(recipient);
            helper.setSubject(subject);


            // Establece el contenido como HTML
            helper.setText(content, true);
            //ESTA LINEA ENVIA EL MAIL DE RECORDATORIO DE FORMA CORRECTA!!
            javaMailSender.send(mimeMessage);

    }


    @Transactional
    public void checkPetsMedicalRecords() throws MessagingException{
        List<Customer> customerList = customerRepository.findAll();
        // Cargar los IDs de mascotas enviados desde un archivo de registro
        Set<String> idsEnviados = cargarIDsEnviadosDesdeArchivo("registro.txt");

        for (Customer customer : customerList) {
            List<Pet> pets = customer.getPets();

            for (Pet pet : pets) {
                List<MedicalRecord> medicalRecords = pet.getMedicalRecords();

                // Verifica si hay registros médicos
                if (!medicalRecords.isEmpty()) {
                    // Ordena los registros médicos por fecha en orden descendente
                    medicalRecords.sort(Comparator.comparing(MedicalRecord::getRecordDate).reversed());

                    // Obtiene el último registro médico
                    MedicalRecord lastMedicalRecord = medicalRecords.get(0);

                    // Verifica si el último registro médico fue hace 6 meses o más
                    if (isOlderThanSixMonths(lastMedicalRecord.getRecordDate())) {
                        // La mascota tiene un registro médico hace 6 meses o más
                        Long petId = pet.getId();

                        // Realiza la acción que necesites con esta información
                        // Por ejemplo, notificar al cliente o tomar alguna otra acción
                        String petname = pet.getPetName();
                        LocalDateTime date = lastMedicalRecord.getRecordDate();
                        if (idsEnviados.contains(petId.toString())) {
                            System.out.println("Correo electrónico ya enviado a la mascota con ID " + petId);
                        } else {
                            System.out.println("Enviar correo electrónico a la mascota con ID " + petId);
                            // Aquí agregarías el código para enviar el correo
                            sendEmailWhenLastMedicalRecordSixMonths(customer, petname, date);
                            // Agregar el ID de la mascota al conjunto de IDs enviados
                            idsEnviados.add(petId.toString());

                            // Guardar los IDs enviados en el archivo de registro
                            guardarIDsEnviadosEnArchivo(idsEnviados, "registro.txt");
                        }

                    }
                }
            }
        }
    }

    public boolean isOlderThanSixMonths(LocalDateTime recordDate) {
        // Obtén la fecha y hora actual
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Calcula la fecha y hora hace 6 meses desde la fecha actual
        LocalDateTime sixMonthsAgoDateTime = currentDateTime.minusMonths(6);

        // Compara la fecha y hora del registro médico con la fecha y hora hace 6 meses
        return recordDate.isBefore(sixMonthsAgoDateTime);
    }



    // Este método carga los IDs de mascotas enviados desde un archivo de registro
    public Set<String> cargarIDsEnviadosDesdeArchivo(String archivo){
        Set<String> idsEnviados = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                idsEnviados.add(linea);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return idsEnviados;
    }

    // Este método guarda los IDs de mascotas enviados en un archivo de registro
   public void guardarIDsEnviadosEnArchivo(Set<String> idsEnviados, String archivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            for (String id : idsEnviados) {
                writer.write(id);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    public void updateCustomerDTO(CustomerDTO customerDTO, Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
        customerRepository.findByEmail(customerDTO.getEmail()).ifPresent( c -> {
            throw new BadRequestException("Email already in use");
        });
        customer =  utilityService.convertCustomerDTOtoCustomerUpdate(customerDTO, customer);
        List<Pet> existingPets = customer.getPets();
        if(!customerDTO.getPets().isEmpty()) {
            for (Pet pet : customerDTO.getPets()) {
                if (pet.getId() != null) {
                    Pet existingPet = existingPets.stream()
                            .filter(pet1 -> pet1.getId().equals(pet.getId()))
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException("Pet not found"));
                    modelMapper.map(pet, existingPet);
                } else {
                    utilityService.createPet(customer, pet);
                }
            }
        }
        String encodedPassword = this.passwordEncoder.encode(customerDTO.getPassword());
        customer.setPassword(encodedPassword);
        Role role = roleService.findById(customerDTO.getRoleId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROLE));
        customer.setRole(role);
        customerRepository.save(customer);
    }


    public List<Customer> getAllCustomers(){return customerRepository.findAll();}

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
    }
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
        customerRepository.delete(customer);
    }

    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
    }

    public List<Customer> findAllAsc(){
        return customerRepository.findAllAsc();
    }

    public Long countCustomers(){
        return customerRepository.countCustomers();
    }

    public Long countCustomersByPetSpecies(String petSpecies) {
        return customerRepository.countCustomersByPetSpecies(petSpecies);
    }

    public List<Customer> listOfCustomersByPetSpecies(String petSpecies){
        return customerRepository.findTheCustomersByPetSpecies(petSpecies);
    }

    public List<Customer> listOfCustomersRole(){
        return customerRepository.findAllCustomerByRole();
    }

    public List<Customer> listCustomersWithSeniorPets(){
        return customerRepository.findOldPets();
    }

    public List<Pet> findPetsByCustomerLastName(String lastName){
        List<Pet> pets = customerRepository.findPetsByCustomerLastName(lastName);
        if (pets.isEmpty()) {
            throw new NotFoundException("No pets found for customer with last name: " + lastName);
        }
        return pets;
    }

    public List<Customer> findByLastName(String lastName){
        List<Customer> customers = customerRepository.findByLastName(lastName);
        if(customers.isEmpty()) {
            throw new NotFoundException("No pets found for customer with last name: " + lastName);
        }
        return customers;
    }

    public Customer findByLastNameAndAnddress(String lastName, String address){
        return customerRepository.findByLastNameAndAddress(lastName, address).orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
    }

    public List<Customer> findCustomersByPetName(String petName) {
        List<Customer> customers = customerRepository.findCustomersByPetName(petName);
        if(customers.isEmpty()){
            throw new NotFoundException("No pets found for customer with last name");
        }
        return customers;
    }

    public void addAnimalToCustomer(Long customerId, Pet pet){
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
        customer.getPets().add(pet);
        customerRepository.save(customer);
    }

    public void addMultiplePetsToCustomer(Long customerId, List<Pet> pets){
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
        List<Pet> petList = customer.getPets();
        for(Pet pet: pets){
            petList.add(pet);
            customer.setPets(petList);
        }
        customerRepository.save(customer);
    }

    public void addRoleToCustomer(Long customerId, Long roleId){
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
        roleService.findById(roleId).ifPresentOrElse(
                foundRole -> {
                    customer.setRole(foundRole);
                    customerRepository.save(customer);
                },
                () -> {
                    throw new NotFoundException(NOT_FOUND_ROLE);
                }
        );
        customerRepository.save(customer);
    }


    public void deletePetsById(Long customerId, List<Long> petIds){
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
        List<Pet> pets = customer.getPets();
            List<Pet> petsToRemove = new ArrayList<>();
            for (Long petId : petIds) {
                // La variable pet se sobreescribe en cada vuelta, guardando el objeto Pet que contenga el id proporcionado en petIds.
                Pet pet = pets.stream().filter(p -> p.getId().equals(petId)).findFirst().orElseThrow(() -> new NotFoundException("Pet with id: " + petId + " does not belong to the customer"));
                if (pet != null) {
                    petsToRemove.add(pet);
                }
                pets.removeAll(petsToRemove);
                customer.setPets(pets);
            }
        customerRepository.save(customer);
    }

    public void deletePetById(Long customerId, Long petId){
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException(NOT_FOUND_CUSTOMER));
        List<Pet> pets = customer.getPets();
        Pet pet = pets.stream().filter(p -> p.getId().equals(petId)).findFirst().orElseThrow(() -> new NotFoundException("Pet not found"));
        pets.remove(pet);
        customer.setPets(pets);
        customerRepository.save(customer);
    }

    public List<Customer> findCustomerByRoleId(Long id){
        return customerRepository.findCustomerByRoleId(id);
    }


    public void deleteRoleById(Long customerId, Long roleId){
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if(optionalCustomer.isPresent()){
            Customer customer = optionalCustomer.get();
            if(customer.getRole() !=null && customer.getRole().getId().equals(roleId)){
                customer.setRole(null);
                customerRepository.save(customer);
                //roleService.delete(roleId); Si agrego esta linea, borra el rol del customer y el rol de la base de datos.
            }else {
                throw new NotFoundException("Role not found with the id " + roleId);
            }
            }else {
                throw new NotFoundException(NOT_FOUND_CUSTOMER);
            }
        }

        public List<CustomerWithNeuteredPetDTO> neuteredPets(){
            return customerRepository.findAllNeuteredAnimals();

        }

    }







