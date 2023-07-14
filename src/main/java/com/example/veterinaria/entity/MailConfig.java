package com.example.veterinaria.entity;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.slf4j.Logger;

@ComponentScan
public class MailConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailConfig.class);

    @Value("${smtp.gmail.com}")
    private String host;

    @Value("${587}")
    private int port;

    @Value("${limonjava1@gmail.com}")
    private String username;

    @Value("${knceoaylagavzwfx}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        LOGGER.info("Initializing JavaMailSender with the following properties:");
        LOGGER.info("Host: {}", host);
        LOGGER.info("Port: {}", port);
        LOGGER.info("Username: {}", username);
        LOGGER.info("Password: {}", password);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        return mailSender;
    }
}
