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
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service

public class AppointmentService {


    private final AppointmentRepository appointmentRepository;

    private final VetService vetService;
    private final CustomerService customerService;

    private final PetService petService;



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


    public Appointment createAppointment(AppointmentDTO appointmentDTO) {

        Appointment appointment = new Appointment();

        List<Long> appPetIds = appointmentDTO.getPetIds();

        Optional<Customer> optionalCustomer = customerService.getCustomerById(appointmentDTO.getCustomer_id());
        if(optionalCustomer.isEmpty()) {
            throw new NotFoundException("customerId " + optionalCustomer+ " not found");

        }
        Customer customer = optionalCustomer.get();
        optionalCustomer.ifPresent(appointment::setCustomer);

        Optional<Vet> optionalVet = vetService.getVetById(appointmentDTO.getVet_id());
        optionalVet.ifPresent(appointment::setVet);

        List<Pet> selectedPets = customer.getPets().stream()
                .filter(pet -> appPetIds.contains(pet.getId()))
                .collect(Collectors.toList());

        if (selectedPets.containsAll(appPetIds)) {
            throw new NotFoundException("One or more petIds not found for the customer");
        }

        appointment.setAppointmentNotes(appointmentDTO.getAppointmentNotes());
        appointment.setAppointmentReason(appointmentDTO.getAppointmentReason());
        appointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime());
        appointment.setPets(selectedPets);
        return appointmentRepository.save(appointment);
    }

    //UPDATE (PUT PATCH REQUESTS)


    public void updateAppointmentDTO(AppointmentDTO appointmentDTO, Long id) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();

            LocalDateTime appDateTime = appointmentDTO.getAppointmentDateTime();
            String appReason = appointmentDTO.getAppointmentReason();
            String appNotes = appointmentDTO.getAppointmentNotes();
            List<Long> appPetIds = appointmentDTO.getPetIds();
            Long customer = appointmentDTO.getCustomer_id();
            Long vet = appointmentDTO.getVet_id();

            if (appDateTime != null && !appDateTime.equals("")) {
                appointment.setAppointmentDateTime(appDateTime);
            }

            if (appReason != null && !appReason.isEmpty()) {
                appointment.setAppointmentNotes(appReason);
            }

            if (appNotes != null && !appNotes.isEmpty()) {
                appointment.setAppointmentReason(appNotes);
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


    public void updateAppointment(Long appointmentId, Appointment appointment) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isPresent()) {
            Appointment existingAppointment = optionalAppointment.get();

            if(appointment.getAppointmentDateTime() !=null && !appointment.getAppointmentDateTime().equals(""))
                existingAppointment.setAppointmentDateTime(appointment.getAppointmentDateTime());
            if (StringUtils.isNotBlank(appointment.getAppointmentReason())) {
                existingAppointment.setAppointmentReason(appointment.getAppointmentReason());
            }
            if (StringUtils.isNotBlank(appointment.getAppointmentNotes())) {
                existingAppointment.setAppointmentNotes(appointment.getAppointmentNotes());
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


    // ALSO DELETES MANY APPOINTEMTS
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



}

