package com.example.veterinaria.service;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Mail;
import com.example.veterinaria.repository.MailRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;




@AllArgsConstructor
@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    private final SpringTemplateEngine templateEngine;

    private static final String sender = "noreply@vethome.com";


    public void sendEmail(Mail mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mail.getSender());
        message.setTo(mail.getRecipient());
        message.setSubject(mail.getSubject());
        message.setText(mail.getContent());

        javaMailSender.send(message);
    }


    public void sendRegistrationEmail(@NotNull Customer customer) {
        String recipient = customer.getEmail();
        String subject = "Registry exitoso en VETHOME";

        Context context = new Context();
        context.setVariable("name", customer.getName());

        // Procesar la plantilla Thymeleaf con el Context
        String content = templateEngine.process("email-template", context);

        Mail mail = new Mail();
        mail.setSender(sender);
        mail.setRecipient(recipient);
        mail.setSubject(subject);
        mail.setContent(content);
        sendEmail(mail);
    }




}
