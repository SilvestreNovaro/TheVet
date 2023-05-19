package com.example.veterinaria.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.veterinaria.DTO.AppointmentDTO;
import com.example.veterinaria.entity.Appointment;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AppointmentService {


    private final AppointmentRepository appointmentRepository;

    private final VetService vetService;
    private final CustomerService customerService;

    private final PetService petService;

    public Appointment createAppointment(AppointmentDTO appointmentDTO) {

        LocalDateTime appLocalDate = appointmentDTO.getAppointmentDateTime();
        String appReason = appointmentDTO.getAppointmentReason();
        String appNotes = appointmentDTO.getAppointmentNotes();
        Long appCustomerId = appointmentDTO.getCustomer_id();
        Long appVetId = appointmentDTO.getVet_id();
        List<Long> appPetIds = appointmentDTO.getPetIds();
        // 1,2, 3.



        Appointment appointment = new Appointment();
        Optional<Customer> optionalCustomer = customerService.getCustomerById(appCustomerId);
        if(optionalCustomer.isEmpty()) {
            throw new NotFoundException("customerId " + appCustomerId + " not found");

        }
        Customer customer = optionalCustomer.get();

        optionalCustomer.ifPresent(appointment::setCustomer);

        Optional<Vet> optionalVet = vetService.getVetById(appVetId);
        optionalVet.ifPresent(appointment::setVet);

        // customer.getPets(): Obtiene la lista de mascotas (pets) del objeto customer
        // .stream(): Convierte la lista de mascotas en un flujo de elementos, lo cual permite realizar operaciones de filtrado y transformación.
        List<Pet> selectedPets = customer.getPets().stream()
                // .filter(pet -> appPetIds . Se aplica un filtro al flujo de mascotas para incluir solo aquellas mascotas cuyos identificadores (pet.getId()) están presentes en la lista de identificadores de mascotas proporcionada (appPetIds) por el cliente. La expresión appPetIds.contains(pet.getId()) comprueba si el identificador de la mascota está presente en la lista de identificadores.

                .filter(pet -> appPetIds.contains(pet.getId()))
                // .collect(Collectors.toList()): Recopila los elementos del flujo en una lista, devolviendo una lista de mascotas seleccionadas (selectedPets).
                .collect(Collectors.toList());
        // if (selectedPets.size() != appPetIds.size()): Verifica si el tamaño de la lista de mascotas seleccionadas es diferente al tamaño de la lista de appPetIds. Si son diferentes, significa que uno o más IDs de mascotas no se encontraron en la lista de mascotas del cliente.
        // Es decir, si yo en postman pongo ids 1,2,3 en appPetIds se guarda 1,2 y 3. Pero quizas en selectedPets no se encontraron todos los ids porque no son de ese cliente, entonces una lista puede quedar mas chica que la otra, significando que el id de la mascota no es de ese cliente.
        if (selectedPets.size() != appPetIds.size()) {
            // 1 ,2                     //1,2,3
            //throw new NotFoundException("One or more petIds not found for the customer"): Lanza una excepción NotFoundException con un mensaje indicando que uno o más IDs de mascotas no se encontraron para el cliente.
            throw new NotFoundException("One or more petIds not found for the customer");
        }

        appointment.setAppointmentNotes(appNotes);
        appointment.setAppointmentReason(appReason);
        appointment.setAppointmentDateTime(appLocalDate);
        appointment.setPets(selectedPets);
        //appointment.setPets(pets);


        return appointmentRepository.save(appointment);
    }



    public void updateAppointment(AppointmentDTO appointmentDTO, Long id) {


        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        LocalDateTime appDateTime = appointmentDTO.getAppointmentDateTime();
        String appReason = appointmentDTO.getAppointmentReason();
        String appNotes = appointmentDTO.getAppointmentNotes();
        List<Long> appPetIds = appointmentDTO.getPetIds();
        System.out.println("pets ids " + appPetIds);


        var customer = appointmentDTO.getCustomer_id();
        System.out.println("customer id " + customer);
        var vet = appointmentDTO.getVet_id();


        if (optionalAppointment.isPresent()) {
            Appointment appointment1 = optionalAppointment.get();
            //System.out.println(appointment1.getAppointmentNotes().toString());
            if (appDateTime != null && !appDateTime.equals("")) appointment1.setAppointmentDateTime(appDateTime);
            if (appReason != null && !appReason.isEmpty()) appointment1.setAppointmentNotes(appReason);
            if (appNotes != null && !appNotes.isEmpty()) appointment1.setAppointmentReason(appNotes);
            if (customer != null && !customer.equals("")) {
                Optional<Customer> optionalCustomer = customerService.getCustomerById(customer);
                if (optionalCustomer.isPresent()) {
                    Customer customerObj = optionalCustomer.get();
                    //Optional<Customer> customerOptional = customerService.findPetsByCustomerName(customerObj.getName().equals(pet))
                    System.out.println("customer name " + customerObj.getName().toString());
                    // Verificar si las mascotas proporcionadas pertenecen al cliente
                    List<Pet> validPets = new ArrayList<>();
                    System.out.println("valid pets array " + validPets);
                    for (Long petId : appPetIds) {
                        System.out.println("pets ids on for " + petId);
                        Optional<Pet> optionalPet = petService.getPetById(petId);
                        System.out.println("pet name " + optionalPet.get().getPetName().toString());
                        //if (optionalPet.isPresent() && appointment1.getCustomer().equals(customerObj)) {
                        Pet pet = optionalPet.get();
                        System.out.println("Pet pet " + pet.getPetName().toString());
                        //List<Pet> customerPets = customerService.findPetsByCustomerName(customerObj.getName());
                        List<Pet> customerPets = customerService.findPetsByCustomerLastName(customerObj.getLastName());
                        System.out.println("customer pets " + customerPets.toString());
                        for (Pet customerPet : customerPets) {
                            System.out.println("pets en el for de customer " + customerPet.getPetName().toString());
                            //no entra a este if
                            if (customerPet.getPetName().equals(pet.getPetName())) {
                               // validPets.add(customerPet);
                                validPets.add(customerPet);
                                System.out.println("lo que guardo al final " + validPets);
                            }
                        }
                            //validPets.add(customerPet);
                            System.out.println("valid pets" + validPets);

                            appointment1.setPets(validPets);
                            appointment1.setCustomer(customerObj);
                        }
                    }
                    if (vet != null && !vet.equals("")) {
                        Optional<Vet> vetOptional = vetService.getVetById(vet);
                        if (vetOptional.isPresent()) {
                            Vet vetObj = vetOptional.get();
                            appointment1.setVet(vetObj);
                        }
                    }
                    appointmentRepository.save(appointment1);
                }
            }
        }


    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }



    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByCustomerId(Long customerId) {
        return appointmentRepository.findByCustomerId(customerId);
    }


    public void deleteAppointmentId(Long id) {
        appointmentRepository.deleteById(id);
    }


    public List<Appointment> findByVetId(Long vetId){
        return appointmentRepository.findByVetId(vetId);
    }

    public List<Appointment> findByLicense(String license){
        return appointmentRepository.findByVetLicense(license);
    }

    /*
    public Long[] deleteAppointmentsByIds(Long[] appointmentIds) {
        // Recibe por parametro un array de ids de appointment.
        //Creo una lista vacia para guardar los ids eliminados
        List<Long> deletedIds = new ArrayList<>();
        //Creo una lista vacia para guardaar los ids que no encontre.
        List<Long> notFoundIds = new ArrayList<>();

        //Recorro el array de appointment ids.
        for (Long appointmentId : appointmentIds) {
            //Dentro del for, se utiliza el método findById para buscar un appointmemnt con el ID actual en la lista de appointments.
            Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
            // Si encuentra el appointment, se elimina y se agrega el id del appointment a la lista de ids eliminados.
            if (optionalAppointment.isPresent()) {
                appointmentRepository.deleteById(optionalAppointment.get().getId());
                deletedIds.add(appointmentId);
            } else {
                // Si no se encuentra se agrega el id a la lista de los no encontrados.
                notFoundIds.add(appointmentId);
            }
        }
        //Pregunto si la lista de ids no encontrados esta vacia. Si esta vacia, devuelve la lista de ids eliminados convertida a un array del tipo Long.
        // Si la lista de ids no encontrados no esta vacia, devuelve la lista de ids no encontrados convertida a un array del tipo Long.
        if (notFoundIds.isEmpty()) {
            return deletedIds.toArray(new Long[0]);
        } else {
            return notFoundIds.toArray(new Long[0]);
        }
    }

     */
    public ResponseEntity<?> deleteAppointmentsByIds(Long[] appointmentIds) {
        List<Long> deletedIds = new ArrayList<>();
        List<Long> notFoundIds = new ArrayList<>();

        for (Long appointmentId : appointmentIds) {
            Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
            if (optionalAppointment.isPresent()) {
                appointmentRepository.deleteById(appointmentId);
                deletedIds.add(appointmentId);
            } else {
                notFoundIds.add(appointmentId);
            }
        }

        if (!deletedIds.isEmpty()) {
            return ResponseEntity.ok("The following appointments have been deleted: " + deletedIds);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No appointments for the ids " + notFoundIds);
        }
    }


    @Transactional
    public List<Long> deleteAppointment(List<Long> appointmentIds) {
        List<Long> InexistentIds = new ArrayList<>();

        for (Long idAppointment : appointmentIds) {
            Optional<Appointment> appointmentOptional = appointmentRepository.findById(idAppointment );
            if (appointmentOptional.isPresent()) {
                appointmentRepository.delete(appointmentOptional.get());
            } else {
                InexistentIds.add(idAppointment );
            }
        }

        return InexistentIds;
    }

   public Optional<Appointment> findByAppointmentDateTime(LocalDateTime appointmentDateTime) {
       return appointmentRepository.findByAppointmentDateTime(appointmentDateTime);
   }

   public List<Appointment> findByPetsId(Long petsId){
        return appointmentRepository.findByPetsId(petsId);
   }

}

