package com.example.veterinaria.service;

import com.example.veterinaria.DTO.CustomerDTO;
import com.example.veterinaria.convert.CustomerDTOConverter;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Role;
import com.example.veterinaria.exception.BadRequestException;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.CustomerRepository;
import com.example.veterinaria.repository.PetRepository;
import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

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

    private final MailService mailService;

    @Autowired
    CustomerDTOConverter customerDTOConverter;



    public void createCustomer(CustomerDTO customerDTO) {
        customerRepository.findByEmail(customerDTO.getEmail()).ifPresent( c -> {
            throw new BadRequestException("Email already in use");
        });
        Customer customer = customerDTOConverter.convertCustomerDTOtoCustomer(customerDTO);
        String encodedPassword = this.passwordEncoder.encode(customerDTO.getPassword());
        customer.setPassword(encodedPassword);
        Role role = roleService.findById(3L).orElseThrow(() -> new NotFoundException("Role not found"));
        customer.setRole(role);
        mailService.sendRegistrationEmail(customer);
        customerRepository.save(customer);
    }


   public void updateCustomerDTO(CustomerDTO customerDTO, Long id){


        Customer customer = customerRepository.findById(id).orElseThrow(() -> new NotFoundException("Customer not found"));
        List<Pet> existingPets = customer.getPets();

        ModelMapper modelMapper = new ModelMapper();
        // Update existing pets or add new ones
        for (Pet petDTO : customerDTO.getPets()) {
            if (petDTO.getId() != null) {
                // Existing pet, find and update
                Pet existingPet = existingPets.stream()
                        .filter(pet -> pet.getId().equals(petDTO.getId()))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Pet not found"));
                // Update existing pet properties
                // You can add more properties here
                existingPet.setPetName(petDTO.getPetName());
                existingPet.setAge(petDTO.getAge());
                existingPet.setGender(petDTO.getGender());
                existingPet.setPetSpecies(petDTO.getPetSpecies());
            } else {
                // New pet, add to the list
                existingPets.add(modelMapper.map(petDTO, Pet.class));
            }
        }

        modelMapper.getConfiguration().setPropertyCondition(ctx -> ctx.getSource() != null && !ctx.getSource().equals(""));
        String encodedPassword = this.passwordEncoder.encode(customerDTO.getPassword());
        roleService.findById(customerDTO.getRoleId()).ifPresentOrElse(c -> customer.setRole(c), () -> {
            throw new NotFoundException("No role found");
        });
        customer.setPassword(encodedPassword);
        modelMapper.map(customerDTO, customer);
        customerRepository.save(customer);
    }



    public void up(CustomerDTO customerDTO, Long id){
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        optionalCustomer.orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
        optionalCustomer.ifPresent(customer -> {
            customer.setName(customerDTO.getName());
            customer.setLastName(customerDTO.getLastName());
            customer.setEmail(customerDTO.getEmail());
            customer.setAddress(customerDTO.getAddress());
            customer.setPets(customerDTO.getPets());
            customer.setContactNumber(customerDTO.getContactNumber());
            roleService.findById(customerDTO.getRoleId()).ifPresent(customer::setRole);
            if (isNotBlank(customerDTO.getPassword())) {
                String encodedPassword = this.passwordEncoder.encode(customerDTO.getPassword());
                customer.setPassword(encodedPassword);
            }
            customerRepository.save(customer);
        });

    }






    // QUEDA IRRELEVANTE, SOLO ME DA LA POSIBILIDAD DE NO PASAR EL ID DEL ROL COMO PARAMETRO A ACTUALIZAR.
    public void updateCustomer(Long id, Customer customer) {
        Customer customer1 = customerRepository.findById(id).orElse(null);
        if (customer1 != null) {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setPropertyCondition(ctx -> ctx.getSource() != null && !ctx.getSource().equals(""));
            modelMapper.map(customer, customer1);

            if (customer.getPassword() != null && !customer.getPassword().isEmpty()) {
                String encodedPassword = this.passwordEncoder.encode(customer.getPassword());
                customer1.setPassword(encodedPassword);
            }

            customerRepository.save(customer1);
        }
    }



    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }


    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public Optional<Customer> findByName(String name){
        return customerRepository.findByName(name);
    }


    public Optional<Customer> findByEmail(String email){
        return customerRepository.findByEmail(email);
    }

    public Optional<Customer> findById(Long id){
        return customerRepository.findById(id);
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

    public List<Pet> findPetsByCustomerName(String name) {
        return customerRepository.findPetsByCustomerName(name);
    }

    public List<Pet> findPetsByCustomerLastName(String lastName){
        return customerRepository.findPetsByCustomerLastName(lastName);
    }

    public Optional<Customer> findByLastName(String lastName){
        return customerRepository.findByLastName(lastName);
    }

    public Optional<Customer> findByLastNameAndAnddress(String lastName, String address){
        return customerRepository.findByLastNameAndAddress(lastName, address);
    }

    public List<Customer> findCustomersByPetName(String petName) {
        return customerRepository.findCustomersByPetName(petName);
    }


    public void addAnimalToCustomer(Long customerId, Pet pet){
        Customer customer = customerRepository.findById(customerId).get();
        customer.getPets().add(pet);
        customerRepository.save(customer);
    }


    public void addPetToCustomer(Long customerId, Long petId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        Pet pet = petService.getPetById(petId).orElseThrow(() -> new RuntimeException("Pet not found"));
        Pet newPet = new Pet();
        newPet.setId(pet.getId());
        newPet.setPetName(pet.getPetName());
        newPet.setAge(pet.getAge());
        newPet.setGender(pet.getGender());
        newPet.setPetSpecies(pet.getPetSpecies());
        customer.getPets().add(newPet);
        customerRepository.save(customer);
    }



    public void addMultiplePetsToCustomer(Long customerId, List<Pet> pets){
        Customer customer = customerRepository.findById(customerId).get();
        List<Pet> petList = customer.getPets();
        List<Pet> petList1 = new ArrayList<>();
        for(Pet pet: pets){
            if(pets.isEmpty()){
                petList1.add(pet);
                petList.addAll(petList1);
                customer.setPets(petList);
            }
        }
        customerRepository.save(customer);

    }

    public void addRoleToCustomer(Long customerId, Role role){
        Customer customer = customerRepository.findById(customerId).get();
        customer.setRole(role);
        customerRepository.save(customer);
    }




    public void deletePetsById(Long customerId, List<Long> petIds){
        Customer customer = customerRepository.findById(customerId).get();
        List<Pet> pets = customer.getPets();
        List<Pet> petsToRemove = new ArrayList<>();
        for(Long petId : petIds){

            // La variable pet se sobreescribe en cada vuelta, guardando el objeto Pet que contenga el id proporcionado en petIds.
            Pet pet = pets.stream().filter(p-> p.getId().equals(petId)).findFirst().orElse(null);
            if(pet !=null){
               petsToRemove.add(pet);

            }
            pets.removeAll(petsToRemove);
            customer.setPets(pets);

        }

        customerRepository.save(customer);
    }




        public void deletePetById(Long customerId, Long petId) {
            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                List<Pet> pets = customer.getPets();
                Optional<Pet> optionalPet = pets.stream().filter(p -> p.getId().equals(petId)).findFirst();
                if (optionalPet.isPresent()) {
                    Pet pet = optionalPet.get();
                    pets.remove(pet);
                    customer.setPets(pets);
                    customerRepository.save(customer);
                } else {
                    throw new NotFoundException("Pet not found with id: " + petId);
                }
            } else {
                throw new NotFoundException("Customer not found with id: " + customerId);
            }
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
                throw new NotFoundException("Customer not found with the id " + customerId);
            }


        }


    }







