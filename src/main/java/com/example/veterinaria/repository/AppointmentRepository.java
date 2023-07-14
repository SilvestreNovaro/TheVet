package com.example.veterinaria.repository;

import com.example.veterinaria.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByCustomerId(Long customerId);

    List<Appointment> findByPetsId(Long petIds);


    Optional<Appointment> findByAppointmentDateTime(LocalDateTime appointmentDateTime);

    List<Appointment> findByVetId(Long vetId);

    List<Appointment> findByVetLicense(String license);

    @Query("SELECT a FROM Appointment a WHERE DATE(a.appointmentDateTime) = :date")
    List<Appointment> findAppointmentsByDate(@Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime >= :startOfDay AND a.appointmentDateTime < :endOfDay")
    List<Appointment> findAppointmentsForTomorrow(LocalDateTime startOfDay, LocalDateTime endOfDay);

}
