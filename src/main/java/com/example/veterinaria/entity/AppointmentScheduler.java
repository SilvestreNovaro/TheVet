package com.example.veterinaria.entity;

import com.example.veterinaria.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



import java.time.LocalDate;

@Component
public class AppointmentScheduler {

    public static final Logger logger = LoggerFactory.getLogger(AppointmentScheduler.class);

    private final AppointmentService appointmentService;
    @Autowired
    public AppointmentScheduler(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }


    @Scheduled(cron = "0 50 18 * * ?") // Se ejecuta todos los días a las 9:50 AM

    public void sendAppointmentReminders() {

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        appointmentService.sendAppointmentNotifications();

        logger.info("Se envían recordatorios de citas para el día: {}", tomorrow);

    }
}
