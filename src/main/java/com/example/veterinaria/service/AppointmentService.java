package com.example.veterinaria.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service

public class AppointmentService {


    private final AppointmentRepository appointmentRepository;

    private final VetService vetService;
    private final CustomerService customerService;

    private final PetService petService;

    @Autowired
    private JavaMailSender javaMailSender;


    private final MailService mailService;



    // FIND(GET REQUESTS)
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }


    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByCustomerId(Long customerId) {
        return appointmentRepository.findByCustomerId(customerId);
    }

    public List<Appointment> findByVetId(Long vetId){
        return appointmentRepository.findByVetId(vetId);
    }

    public List<Appointment> findByLicense(String license){
        return appointmentRepository.findByVetLicense(license);
    }

    public Optional<Appointment> findByAppointmentDateTime(LocalDateTime appointmentDateTime) {
        return appointmentRepository.findByAppointmentDateTime(appointmentDateTime);
    }

    public List<Appointment> findByPetsId(Long petsId){
        return appointmentRepository.findByPetsId(petsId);
    }




    // CREATE (POST REQUEST)


    public void createAppointment(AppointmentDTO appointmentDTO) {

        Appointment appointment = new Appointment();

        List<Long> appPetIds = appointmentDTO.getPetIds();

        Optional<Customer> optionalCustomer = customerService.getCustomerById(appointmentDTO.getCustomerId());


        optionalCustomer.ifPresent(appointment::setCustomer);
        Customer customer = optionalCustomer.get();

        Optional<Vet> optionalVet = vetService.getVetById(appointmentDTO.getVetId());
        optionalVet.ifPresent(appointment::setVet);

        List<Pet> selectedPets = customer.getPets().stream()
                .filter(pet -> appPetIds.contains(pet.getId()))
                .collect(Collectors.toList());

        if (!selectedPets.stream().map(Pet::getId).collect(Collectors.toList()).containsAll(appPetIds)) {
            throw new NotFoundException("One or more petIds not found for the customer");
        }

        appointment.setAppointmentReason(appointmentDTO.getAppointmentReason());
        appointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime());
        appointment.setPets(selectedPets);
         appointmentRepository.save(appointment);
    }

    //UPDATE (PUT PATCH REQUESTS)


    // Funciona. Quitar validaciones service? @NotBlank en entity.
    public void updateAppointmentDTO(AppointmentDTO appointmentDTO, Long id) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();

            LocalDateTime appDateTime = appointmentDTO.getAppointmentDateTime();
            String appReason = appointmentDTO.getAppointmentReason();
            List<Long> appPetIds = appointmentDTO.getPetIds();
            Long customer = appointmentDTO.getCustomerId();
            Long vet = appointmentDTO.getVetId();

            if (appDateTime != null && !appDateTime.equals("")) {
                appointment.setAppointmentDateTime(appDateTime);
            }

            if (appReason != null && !appReason.isEmpty()) {
                appointment.setAppointmentReason(appReason);
            }


            if (customer != null && !customer.equals("")) {
                Optional<Customer> optionalCustomer = customerService.getCustomerById(customer);
                if (optionalCustomer.isPresent()) {
                    Customer customerObj = optionalCustomer.get();
                    List<Pet> validPets = new ArrayList<>();

                    for (Long petId : appPetIds) {
                        Optional<Pet> optionalPet = petService.getPetById(petId);
                        if (optionalPet.isPresent()) {
                            Pet pet = optionalPet.get();
                            if (customerObj.getPets().contains(pet)) {
                                validPets.add(pet);
                            } else {
                                throw new NotFoundException("PetId " + petId + " not found for the customer");
                            }
                        } else {
                            throw new NotFoundException("PetId " + petId + " not found");
                        }
                    }

                    appointment.setPets(validPets);
                    appointment.setCustomer(customerObj);
                }
            }

            if (vet != null && !vet.equals("")) {
                Optional<Vet> vetOptional = vetService.getVetById(vet);
                if (vetOptional.isPresent()) {
                    Vet vetObj = vetOptional.get();
                    appointment.setVet(vetObj);
                }
            }

            appointmentRepository.save(appointment);
        }
    }


    //Funciona.
    public void updateAppointment(Long appointmentId, Appointment appointment) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isPresent()) {
            Appointment existingAppointment = optionalAppointment.get();

            if(appointment.getAppointmentDateTime() !=null && !appointment.getAppointmentDateTime().equals(""))
                existingAppointment.setAppointmentDateTime(appointment.getAppointmentDateTime());
            if (StringUtils.isNotBlank(appointment.getAppointmentReason())) {
                existingAppointment.setAppointmentReason(appointment.getAppointmentReason());
            }

            appointmentRepository.save(existingAppointment);
        } else {
            throw new NotFoundException("Appointment not found with ID: " + appointmentId);
        }
    }




    // DELETE REQUESTS


    public void deleteAppointmentId(Long id) {
        appointmentRepository.deleteById(id);
    }



    public ResponseEntity<String> deleteAppointmentsByIds(Long[] appointmentIds) {
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


    // ALSO DELETES MANY APPOINTEMTS
    @Transactional
    public List<Long> deleteAppointment(List<Long> appointmentIds) {
        List<Long> inexistentIds = new ArrayList<>();

        for (Long idAppointment : appointmentIds) {
            Optional<Appointment> appointmentOptional = appointmentRepository.findById(idAppointment );
            if (appointmentOptional.isPresent()) {
                appointmentRepository.delete(appointmentOptional.get());
            } else {
                inexistentIds.add(idAppointment );
            }
        }

        return inexistentIds;
    }

    public void sendAppointmentNotifications() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime startOfDay = tomorrow.atStartOfDay();
        LocalDateTime endOfDay = tomorrow.plusDays(1).atStartOfDay();

        List<Appointment> appointments = appointmentRepository.findAppointmentsForTomorrow(startOfDay, endOfDay);

        if (!appointments.isEmpty()) {
            for (Appointment appointment : appointments) {

                Customer customer = appointment.getCustomer();
                String email = customer.getEmail();

                StringBuilder petsText = new StringBuilder();
                List<Pet> pets = appointment.getPets();
                int numPets = pets.size();

                for (int i = 0; i < numPets; i++) {
                    Pet pet = pets.get(i);
                    // Agregar el nombre de la mascota
                    petsText.append(pet.getPetName());

                    if (numPets > 1) {
                        // Más de una mascota, manejar casos especiales de separación
                        if (i < numPets - 2) {
                            // No es la última mascota, agregar coma y espacio
                            petsText.append(", ");
                        } else if (i == numPets - 2) {
                            // Penúltima mascota, agregar coma y "y"
                            petsText.append(" y ");
                        }
                    }
                }

// Construir el mensaje del correo electrónico
                String subject = "Recordatorio de Appointment";
                String message = "Estimado " + customer.getName() + ",\n\n"
                        + "Este es un recordatorio amable de que tienes un Appointment programado para mañana.\n"
                        + "Fecha y Hora: " + appointment.getAppointmentDateTime() + "\n"
                        + "Lugar: VETHOME"  + "\n\n"
                        + "Con tu/s mascotas: " + petsText.toString() + "\n\n"
                        + "¡Esperamos verte allí!\n\n"
                        + "Saludos,\n"
                        + "El equipo de tu clínica veterinaria";


                // Envía el correo electrónico al cliente
                sendEmail(email, subject, message);
            }
        }
        else{
            throw new NotFoundException("No se encontraron citas programadas para mañana.");
        }
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    public List<Appointment> allAppointmentsByXDate(LocalDate date){
        return appointmentRepository.findAppointmentsByDate(date);
    }
}

