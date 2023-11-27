package com.example.veterinaria.entity;

import com.example.veterinaria.service.CustomerService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CheckPetsMRSSixMonths {

    public static final Logger logger = LoggerFactory.getLogger(CheckPetsMRSSixMonths.class);

    private final CustomerService customerService;

    @Autowired
    public CheckPetsMRSSixMonths(CustomerService customerService){
        this.customerService = customerService;
    }

    @Scheduled(cron = "0 24 16 * * MON")

    public void SendEmailAdfterSixMonths() throws MessagingException {

        customerService.checkPetsMedicalRecords();

        logger.info("Se envían recordatorios de citas para mascotas que no vienen hace 6 meses");
    }
}
