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
        var appLocalDate = appointmentDTO.getAppointmentDateTime();
        var appReason = appointmentDTO.getAppointmentReason();
        var appNotes = appointmentDTO.getAppointmentNotes();
        var appCustomerId = appointmentDTO.getCustomer_id();
        var appVetId = appointmentDTO.getVet_id();
        //var appPets = appointmentDTO.getPets_ids();

        //List<Pet> pets = petService.getAllPetsIds(appPets);



        Appointment appointment = new Appointment();
        Optional<Customer> optionalCustomer = customerService.getCustomerById(appCustomerId);

        optionalCustomer.ifPresent(appointment::setCustomer);

        Optional<Vet> optionalVet = vetService.getVetById(appVetId);
        optionalVet.ifPresent(appointment::setVet);

        appointment.setAppointmentNotes(appNotes);
        appointment.setAppointmentReason(appReason);
        appointment.setAppointmentDateTime(appLocalDate);
        //appointment.setPets(pets);


        return appointmentRepository.save(appointment);
    }



    public void updateAppointment(AppointmentDTO appointmentDTO, Long id){


        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        var appDateTime = appointmentDTO.getAppointmentDateTime();
        var appReason = appointmentDTO.getAppointmentReason();
        var appNotes = appointmentDTO.getAppointmentNotes();

        var customer = appointmentDTO.getCustomer_id();
        var vet = appointmentDTO.getVet_id();


        if(optionalAppointment.isPresent()){
            Appointment appointment1 = optionalAppointment.get();
            if(appDateTime !=null && !appDateTime.equals("")) appointment1.setAppointmentDateTime(appDateTime);
            if(appReason !=null && !appReason.isEmpty()) appointment1.setAppointmentNotes(appReason);
            if(appNotes!=null && !appNotes.isEmpty()) appointment1.setAppointmentReason(appNotes);
            if(customer!=null && !customer.equals("")) {
                Optional<Customer> optionalCustomer = customerService.getCustomerById(customer);
                if (optionalCustomer.isPresent()) {
                    Customer customerObj = optionalCustomer.get();
                    appointment1.setCustomer(customerObj);
                }

            }
            if(vet!=null && !vet.equals("")){
                Optional<Vet> vetOptional = vetService.getVetById(vet);
                if(vetOptional.isPresent()){
                    Vet vetObj = vetOptional.get();
                    appointment1.setVet(vetObj);
                }
            }

            appointmentRepository.save(appointment1);

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
            return ResponseEntity.ok("Se eliminaron las citas con los siguientes IDs: " + deletedIds);
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


}

