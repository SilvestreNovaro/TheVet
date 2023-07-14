package com.example.veterinaria.entity;

import com.example.veterinaria.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;


@Component
public class AppointmentScheduler {

    private final AppointmentService appointmentService;
    @Autowired
    public AppointmentScheduler(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }


    @Scheduled(cron = "0 41 15 * * ?") // Se ejecuta todos los días a las 9:50 AM

    public void sendAppointmentReminders() {



        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime tomorrowStart = tomorrow.atStartOfDay();
        LocalDateTime tomorrowEnd = tomorrow.atTime(LocalTime.MAX);



        appointmentService.sendAppointmentNotifications();

        System.out.println("Se envían recordatorios de citas para el día: " + tomorrow);
    }
}
