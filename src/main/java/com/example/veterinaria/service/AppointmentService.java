package com.example.veterinaria.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.veterinaria.DTO.AppointmentDTO;
import com.example.veterinaria.entity.Appointment;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AppointmentService {


    private final AppointmentRepository appointmentRepository;

    private final VetService vetService;
    private final CustomerService customerService;

    public Appointment createAppointment(AppointmentDTO appointmentDTO) {
        var appLocalDate = appointmentDTO.getAppointmentDateTime();
        var appReason = appointmentDTO.getAppointmentReason();
        var appNotes = appointmentDTO.getAppointmentNotes();
        var appCustomerId = appointmentDTO.getCustomer_id();
        var appVetId = appointmentDTO.getVet_id();

        Appointment appointment = new Appointment();
        Optional<Customer> optionalCustomer = customerService.getCustomerById(appCustomerId);

        optionalCustomer.ifPresent(appointment::setCustomer);

        Optional<Vet> optionalVet = vetService.getVetById(appVetId);
        optionalVet.ifPresent(appointment:: setVet);

        appointment.setAppointmentNotes(appNotes);
        appointment.setAppointmentReason(appReason);
        appointment.setAppointmentDateTime(appLocalDate);

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

    /*public List<Appointment> getAppointmentsByPetId(Long petId) {
        return appointmentRepository.findByPetId(petId);
    }
*/
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }


    public List<Appointment> findByVetId(Long vetId){
        return appointmentRepository.findByVetId(vetId);
    }

    public List<Appointment> findByLicense(String license){
        return appointmentRepository.findByVetLicense(license);
    }

    public Long[] deleteAppointmentsByIds(Long[] appointmentIds) {
        List<Long> deletedIds = new ArrayList<>();
        List<Long> notFoundIds = new ArrayList<>();

        for (Long appointmentId : appointmentIds) {
            Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
            if (optionalAppointment.isPresent()) {
                appointmentRepository.deleteById(optionalAppointment.get().getId());
                deletedIds.add(appointmentId);
            } else {
                notFoundIds.add(appointmentId);
            }
        }

        if (notFoundIds.isEmpty()) {
            return deletedIds.toArray(new Long[0]);
        } else {
            return notFoundIds.toArray(new Long[0]);
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

   /* public Optional<LocalDateTime> localDate(LocalDateTime localDateTime){
        return appointmentRepository.findByAppointmentDateTime(localDateTime());
    }

    */
   public Optional<Appointment> findByAppointmentDateTime(LocalDateTime appointmentDateTime) {
       return appointmentRepository.findByAppointmentDateTime(appointmentDateTime);
   }


}

