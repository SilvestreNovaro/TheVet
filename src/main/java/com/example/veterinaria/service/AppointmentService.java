package com.example.veterinaria.service;

import java.util.List;
import java.util.Optional;

import com.example.veterinaria.entity.Appointment;
import com.example.veterinaria.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment createAppointment(Appointment appointment) {
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

    public List<Appointment> getAppointmentsByPetId(Long petId) {
        return appointmentRepository.findByPetId(petId);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

}

