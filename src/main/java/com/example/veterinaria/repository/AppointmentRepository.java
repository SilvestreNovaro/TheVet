package com.example.veterinaria.repository;

import com.example.veterinaria.entity.Appointment;
import com.example.veterinaria.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
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

    @Query("SELECT DISTINCT a.customer FROM Appointment a WHERE a.appointmentDateTime BETWEEN :startDate AND :endDate")
    List<Customer> findCustomersWithAppointmentsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    default List<Customer> findCustomersWithAppointmentsNextWeek() {
        // Obtener la fecha del próximo lunes
        LocalDate now = LocalDate.now();
        LocalDate nextMonday = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDateTime startDate = nextMonday.atStartOfDay();

        // Obtener la fecha del próximo sábado
        LocalDate nextSaturday = now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        LocalDateTime endDate = nextSaturday.atTime(LocalTime.MAX);

        // Llamar al método que utiliza la consulta JPQL
        return findCustomersWithAppointmentsBetween(startDate, endDate);
    }
}
