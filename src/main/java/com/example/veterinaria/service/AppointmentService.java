package com.example.veterinaria.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.veterinaria.DTO.AppointmentDTO;
import com.example.veterinaria.entity.Appointment;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.exception.BadRequestException;
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

    private final static String NOT_FOUND_APPOINTMENT = "Appointment not found";


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
    public List<Customer> listOfCustomersWithAppointments(LocalDateTime startDate, LocalDateTime endDate){
        return appointmentRepository.findCustomersWithAppointmentsBetween(startDate, endDate);
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
        boolean isAvailable = appointmentRepository.isAppointmentAvailable(appointmentDTO.getAppointmentDateTime());
        if(isAvailable){
            throw new BadRequestException("An appointment is already created by the exact same time");
        }
        List<Long> appPetIds = appointmentDTO.getPetIds();

        Customer customer = customerService.getCustomerById(appointmentDTO.getCustomerId());
        appointment.setCustomer(customer);

        Optional<Vet> optionalVet = vetService.getVetById(appointmentDTO.getVetId());
        optionalVet.ifPresent(appointment::setVet);

        List<Pet> selectedPets = customer.getPets().stream()
                .filter(pet -> appPetIds.contains(pet.getId()))
                .toList();

        if (!selectedPets.stream().map(Pet::getId).toList().containsAll(appPetIds)) {
            throw new NotFoundException("One or more petIds not found for the customer");
        }

        appointment.setAppointmentReason(appointmentDTO.getAppointmentReason());
        appointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime());
        appointment.setPets(selectedPets);
         appointmentRepository.save(appointment);
    }

    //UPDATE (PUT PATCH REQUESTS)

    @Transactional
    public void updateAppointment(AppointmentDTO appointmentDTO, Long id){
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_APPOINTMENT));
        boolean isAvailable = appointmentRepository.isAppointmentAvailable(appointmentDTO.getAppointmentDateTime());
        if(isAvailable){
        throw new BadRequestException("An appointment is already created by the exact same time");
        }
        Customer customer = customerService.getCustomerById(appointmentDTO.getCustomerId());
        Vet vet = vetService.getVetById(appointmentDTO.getVetId()).orElseThrow(() -> new NotFoundException("No vet found"));
        List<Long> petIds = appointmentDTO.getPetIds();
        List<Pet> petsToAdd = new ArrayList<>();
        for(Long petId : petIds){
            Pet pet = customer.getPets().stream()
                    .filter(p -> p.getId().equals(petId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Pet not found for the customer"));
            petsToAdd.add(pet);
        }
        if(petsToAdd.isEmpty()){
            throw new NotFoundException("Pet not found on our database");
        }else{
            appointment.setPets(petsToAdd);
        }
        appointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime() != null && !appointmentDTO.getAppointmentDateTime().toString().isEmpty() ? appointmentDTO.getAppointmentDateTime() : appointment.getAppointmentDateTime());
        if (StringUtils.isNotBlank(appointmentDTO.getAppointmentReason())) {
            appointment.setAppointmentReason(appointmentDTO.getAppointmentReason());
        }
        appointment.setVet(vet);
        appointment.setCustomer(customer);
        appointmentRepository.save(appointment);
}


    //Funciona.
    public void updateAppointment(Long appointmentId, Appointment appointment1) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new NotFoundException(NOT_FOUND_APPOINTMENT));
            if(appointment1.getAppointmentDateTime() != null && !appointment1.getAppointmentDateTime().toString().isEmpty())
                appointment.setAppointmentDateTime(appointment1.getAppointmentDateTime());
            if (StringUtils.isNotBlank(appointment1.getAppointmentReason())) {
                appointment.setAppointmentReason(appointment1.getAppointmentReason());
            }
            appointmentRepository.save(appointment);
    }


    // DELETE REQUESTS


    public void deleteAppointmentId(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_APPOINTMENT));
        //appointment.getPets().clear(); Si quiero eliminar, y no tener que usar cascade DETACH.
        appointmentRepository.deleteById(id);
    }


    public void deleteAppointmentsByIds(Long[] appointmentIds) {
        List<Long> deletedIds = new ArrayList<>();
        List<Long> notFoundIds = new ArrayList<>();

        for (Long appointmentId : appointmentIds) {
            try {
                // Intenta encontrar y eliminar la cita
                Appointment appointment = appointmentRepository.findById(appointmentId)
                        .orElseThrow(() -> new NotFoundException(NOT_FOUND_APPOINTMENT));
                appointmentRepository.delete(appointment);
                deletedIds.add(appointmentId);
            } catch (NotFoundException ex) {
                // Si no se encuentra la cita, agrega su ID a notFoundIds
                notFoundIds.add(appointmentId);
            }
        }
        if (!notFoundIds.isEmpty()) {
            // Si hay IDs no encontrados, lanza una excepción
            throw new NotFoundException(NOT_FOUND_APPOINTMENT + notFoundIds);
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

    @Transactional
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

