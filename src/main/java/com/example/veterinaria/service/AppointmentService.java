package com.example.veterinaria.service;

import java.util.List;
import java.util.Optional;

import com.example.veterinaria.DTO.AppointmentDTO;
import com.example.veterinaria.entity.Appointment;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.repository.AppointmentRepository;
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

}

