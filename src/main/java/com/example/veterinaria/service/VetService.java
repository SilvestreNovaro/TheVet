package com.example.veterinaria.service;
import com.example.veterinaria.entity.Appointment;
import com.example.veterinaria.entity.AvailabilitySlot;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.exception.BadRequestException;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.AppointmentRepository;
import com.example.veterinaria.repository.AvailabilitySlotRepository;
import com.example.veterinaria.repository.VetRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@Service

public class VetService {

    @Autowired
    private final VetRepository vetRepository;

    @Autowired
    private final AppointmentRepository appointmentRepository;

    private final ModelMapper modelMapper;
    private static final String LICENSE_DUPLICATED = "License already in use";
    private static final String EMAIL_IN_USE = "Email already in use";
    private static final String NOT_FOUND_VET = "No vet found with the id: ";


    public void createVet(Vet vet) {
        vetRepository.findByLicense(vet.getLicense())
                .ifPresent(v -> {
                    throw new BadRequestException(LICENSE_DUPLICATED);
                });
        vetRepository.findByEmail(vet.getEmail())
                .ifPresent(v -> {
                    throw new BadRequestException(EMAIL_IN_USE);
                });

        Vet vetToSave = modelMapper.map(vet, Vet.class);
        vetRepository.save(vetToSave);
    }


    public void updateVet(Vet vet, Long id) {
        Vet existingVet = vetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_VET + id));

        vetRepository.findByLicense(vet.getLicense())
                .ifPresent(v -> {
                    throw new BadRequestException(LICENSE_DUPLICATED);
                });
        vetRepository.findByEmail(vet.getEmail())
                .ifPresent(v -> {
                    throw new BadRequestException(EMAIL_IN_USE);
                });
        modelMapper.map(vet, existingVet);
        vetRepository.save(existingVet);
    }

    public void updateAvailabilitySlots(Long id, List<AvailabilitySlot> availabilitySlots) {
        Vet vet = vetRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_VET + id));
        vet.setAvailabilitySlots(availabilitySlots);
        vetRepository.save(vet);
    }


    public List<Vet> getAllVets() {
        return vetRepository.findAll();
    }

    public Optional<Vet> getVetById(Long id) {
        return vetRepository.findById(id).or(() -> {
            throw new NotFoundException(NOT_FOUND_VET + id);
        });
    }


    public void deleteVet(Long id) {
        vetRepository.findById(id).ifPresentOrElse(
                vet -> vetRepository.deleteById(id),
                () -> {
                    throw new NotFoundException(NOT_FOUND_VET + id);
                }
        );
    }


    public void deleteByLicense(String license) {
        vetRepository.findByLicense(license).ifPresentOrElse(vet -> vetRepository.deleteByLicense(license), () -> {
                    throw new NotFoundException("No vet found with the license " + license);
                }
        );
    }

    public Optional<Vet> findByLicense(String license) {
        return vetRepository.findByLicense(license).or(() -> {
            throw new NotFoundException("No vet found with the license " + license);
        });
    }

    public Optional<Vet> findByName(String name) {
        return vetRepository.findByName(name).or(() -> {
            throw new NotFoundException("No vet found with name " + name);
        });
    }

    public Optional<Vet> findVetByEmail(String email) {
        return vetRepository.findByEmail(email).or(() -> {
            throw new NotFoundException("No vet found with email " + email);
        });
    }

    public void deleteBySurName(String surName) {
        vetRepository.findBySurname(surName).ifPresent(vet -> vetRepository.deleteBySurname(surName));
        throw new NotFoundException("No vet found with the surname: " + surName);
    }

    public List<Vet> listOfVetsBySpecialty(String specialty) {
        return vetRepository.findBySpecialty(specialty);
    }


    public void createVetWithAvailabilitySlots(List<AvailabilitySlot> availabilitySlots, Long vetId) {
        Vet vet = vetRepository.findById(vetId).orElseThrow(() -> new NotFoundException(NOT_FOUND_VET + vetId));
        vet.setAvailabilitySlots(availabilitySlots);
        vetRepository.save(vet);
    }

    public List<AvailabilitySlot> findVetAvailability(Long vetId) {
        Vet vet = vetRepository.findById(vetId).orElseThrow(() -> new NotFoundException(NOT_FOUND_VET + vetId));
        return vet.getAvailabilitySlots();
    }


    public List<LocalDateTime> getOccupiedTimeSlotsForVet(Long vetId) {
        if (vetRepository.existsById(vetId)) {
            // Obtener todas las citas programadas para el veterinario con el ID dado
            List<Appointment> appointments = appointmentRepository.findByVetId(vetId);

            // Crear una lista para almacenar las fechas y horarios ocupados
            List<LocalDateTime> occupiedTimeSlots = new ArrayList<>();

            // Recorrer las citas y agregar sus fechas y horarios a la lista
            for (Appointment appointment : appointments) {
                occupiedTimeSlots.add(appointment.getAppointmentDateTime());
            }

            return occupiedTimeSlots;
        } else {
            throw new NotFoundException(NOT_FOUND_VET + vetId);
        }
    }
}
