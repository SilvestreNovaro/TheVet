package com.example.veterinaria.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.veterinaria.DTO.AppointmentDTO;
import com.example.veterinaria.entity.*;
import com.example.veterinaria.exception.BadRequestException;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.AppointmentRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@AllArgsConstructor
@Service

public class AppointmentService {


    private final AppointmentRepository appointmentRepository;

    private final VetService vetService;
    private final CustomerService customerService;

    private final PetService petService;
    private final SpringTemplateEngine templateEngine;
    private static final String sender = "noreply@vethome.com";
    @Autowired
    private JavaMailSender javaMailSender;


    private final MailService mailService;

    private static final String NOT_FOUND_APPOINTMENT = "Appointment not found";


    // FIND(GET REQUESTS)
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAppointmentByLastNameAndAddress(String lastName, String address){
        return appointmentRepository.findAppointmentFromCustomerLastNameAndAddress(lastName, address);
        }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByCustomerId(Long customerId) {
        return appointmentRepository.findByCustomerId(customerId);
    }
    public List<Customer> listOfCustomersWithAppointments(LocalDateTime startDate, LocalDateTime endDate){
        return appointmentRepository.findCustomersWithAppointmentsBetween(startDate, endDate);
    }

    public List<Appointment> findByVetId(Long vetId){
        return appointmentRepository.findByVetId(vetId);
    }

    public List<Appointment> findByLicense(String license){
        return appointmentRepository.findByVetLicense(license);
    }

    public Optional<Appointment> findByAppointmentDateTime(LocalDateTime appointmentDateTime) {
        return appointmentRepository.findByAppointmentDateTime(appointmentDateTime);
    }

    public List<Appointment> findByPetsId(Long petsId){
        return appointmentRepository.findByPetsId(petsId);
    }

    public List<Appointment> allAppointmentsByXDate(LocalDate date){
        return appointmentRepository.findAppointmentsByDate(date);
    }




    // CREATE (POST REQUEST)

    public void createAppointment(AppointmentDTO appointmentDTO) throws MessagingException {

        Appointment appointment = new Appointment();
        boolean isAvailable = appointmentRepository.isAppointmentAvailable(appointmentDTO.getAppointmentDateTime());
        if(isAvailable){
            throw new BadRequestException("An appointment is already created by the exact same time");
        }
        List<Long> appPetIds = appointmentDTO.getPetIds();

        Customer customer = customerService.getCustomerById(appointmentDTO.getCustomerId());
        appointment.setCustomer(customer);

        Optional<Vet> optionalVet = vetService.getVetById(appointmentDTO.getVetId());
        optionalVet.ifPresent(appointment::setVet);

        List<Pet> selectedPets = customer.getPets().stream()
                .filter(pet -> appPetIds.contains(pet.getId()))
                .toList();

        if (!selectedPets.stream().map(Pet::getId).toList().containsAll(appPetIds)) {
            throw new NotFoundException("One or more petIds not found for the customer");
        }

        appointment.setAppointmentReason(appointmentDTO.getAppointmentReason());
        appointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime());
        appointment.setPets(selectedPets);
        appointmentRepository.save(appointment);
        //sendAppointmentConfirmationEmail(appointment);
        createAppointmentConfirmation(customer, appointmentDTO);
    }

    //UPDATE (PUT PATCH REQUESTS)

    @Transactional
    public void updateAppointment(AppointmentDTO appointmentDTO, Long id){
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_APPOINTMENT));
        boolean isAvailable = appointmentRepository.isAppointmentAvailable(appointmentDTO.getAppointmentDateTime());
        if(isAvailable){
        throw new BadRequestException("An appointment is already created by the exact same time");
        }
        Customer customer = customerService.getCustomerById(appointmentDTO.getCustomerId());
        Vet vet = vetService.getVetById(appointmentDTO.getVetId()).orElseThrow(() -> new NotFoundException("No vet found"));
        List<Long> petIds = appointmentDTO.getPetIds();
        List<Pet> petsToAdd = new ArrayList<>();
        for(Long petId : petIds){
            Pet pet = customer.getPets().stream()
                    .filter(p -> p.getId().equals(petId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Pet not found for the customer"));
            petsToAdd.add(pet);
        }
        if(!petsToAdd.isEmpty()) {
            appointment.setPets(petsToAdd);
        }
        appointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime() != null && !appointmentDTO.getAppointmentDateTime().toString().isEmpty() ? appointmentDTO.getAppointmentDateTime() : appointment.getAppointmentDateTime());
        if (StringUtils.isNotBlank(appointmentDTO.getAppointmentReason())) {
            appointment.setAppointmentReason(appointmentDTO.getAppointmentReason());
        }
        appointment.setVet(vet);
        appointment.setCustomer(customer);
        appointmentRepository.save(appointment);
}


    //Funciona.
    public void updateAppointment(Long appointmentId, Appointment appointment1) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new NotFoundException(NOT_FOUND_APPOINTMENT));
            if(appointment1.getAppointmentDateTime() != null && !appointment1.getAppointmentDateTime().toString().isEmpty())
                appointment.setAppointmentDateTime(appointment1.getAppointmentDateTime());
            if (StringUtils.isNotBlank(appointment1.getAppointmentReason())) {
                appointment.setAppointmentReason(appointment1.getAppointmentReason());
            }
            appointmentRepository.save(appointment);
    }






    // DELETE REQUESTS

    public void deleteAppointmentId(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_APPOINTMENT));
        //appointment.getPets().clear(); Si quiero eliminar, y no tener que usar cascade DETACH.
        appointmentRepository.delete(appointment);
    }


    public void deleteAppointmentsByIds(Long[] appointmentIds) {
        List<Long> deletedIds = new ArrayList<>();
        List<Long> notFoundIds = new ArrayList<>();

        for (Long appointmentId : appointmentIds) {
            try {
                // Intenta encontrar y eliminar la cita
                Appointment appointment = appointmentRepository.findById(appointmentId)
                        .orElseThrow(() -> new NotFoundException(NOT_FOUND_APPOINTMENT));
                appointmentRepository.delete(appointment);
                deletedIds.add(appointmentId);
            } catch (NotFoundException ex) {
                // Si no se encuentra la cita, agrega su ID a notFoundIds
                notFoundIds.add(appointmentId);
            }
        }
        if (!notFoundIds.isEmpty()) {
            // Si hay IDs no encontrados, lanza una excepción
            throw new NotFoundException(NOT_FOUND_APPOINTMENT + notFoundIds);
        }
    }


    // ALSO DELETES MANY APPOINTEMTS
    @Transactional
    public List<Long> deleteAppointment(List<Long> appointmentIds) {
        List<Long> inexistentIds = new ArrayList<>();

        for (Long idAppointment : appointmentIds) {
            Optional<Appointment> appointmentOptional = appointmentRepository.findById(idAppointment );
            if (appointmentOptional.isPresent()) {
                appointmentRepository.delete(appointmentOptional.get());
            } else {
                inexistentIds.add(idAppointment );
            }
        }
        return inexistentIds;
    }






    //APPOINTMENT NOTIFICATIONS

    @Transactional
    public String buildPetsText(List<Pet> pets) {
        StringBuilder petsText = new StringBuilder();
        int numPets = pets.size();

        for (int i = 0; i < numPets; i++) {
            Pet pet = pets.get(i);
            petsText.append(pet.getPetName());

            if (numPets > 1) {
                if (i < numPets - 2) {
                    petsText.append(", ");
                } else if (i == numPets - 2) {
                    petsText.append(" y ");
                }
            }
        }

        return petsText.toString();
    }

    @Transactional
    public void sendAppointmentNotifications() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime startOfDay = tomorrow.atStartOfDay();
        LocalDateTime endOfDay = tomorrow.plusDays(1).atStartOfDay();

        List<Appointment> appointments = appointmentRepository.findAppointmentsForTomorrow(startOfDay, endOfDay);

        if (!appointments.isEmpty()) {
            for (Appointment appointment : appointments) {
                Customer customer = appointment.getCustomer();
                String recipientEmail = customer.getEmail();
                String subject = "Recordatorio de Appointment";

                // Crear el objeto Mail
                Mail mail = new Mail();
                mail.setSender(sender);
                mail.setRecipient(recipientEmail);
                mail.setSubject(subject);

                // Procesar el template Thymeleaf
                Context context = new Context();
                context.setVariable("customerName", customer.getName());
                context.setVariable("appointmentDateTime", appointment.getAppointmentDateTime().toString());
                context.setVariable("petsText", buildPetsText(appointment.getPets()));

                // Procesar la plantilla Thymeleaf con el Context
                String content = templateEngine.process("appointment_notification_template", context);
                mail.setContent(content);

                // Enviar correo utilizando el método sendEmail(Mail mail)
                sendEmail(mail);
            }
        } else {
            throw new NotFoundException("No se encontraron citas programadas para mañana.");
        }
    }


   /* public void sendAppointmentConfirmationEmail(Appointment appointment) throws MessagingException {
        String recipient = appointment.getCustomer().getEmail();
        String subject = "Confirmación de Turno en VETHOME";

        String buildPetsText = buildPetsText(appointment.getPets());

        Context context = new Context();
        context.setVariable("name", appointment.getCustomer().getName());
        context.setVariable("appointmentDateTime", appointment.getAppointmentDateTime().toString());
        context.setVariable("petsText", buildPetsText);

        // Procesar la plantilla Thymeleaf con el Context
        String content = templateEngine.process("appointment_email_template.html", context);

        System.out.println("Contenido generado por Thymeleaf: " + content);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(recipient);
        helper.setSubject(subject);

        // Establece el contenido como HTML
        helper.setText(content, true);


        Mail mail = new Mail();
        mail.setSender(sender);
        mail.setRecipient(recipient);
        mail.setSubject(subject);
        mail.setContent(content);
        sendEmail(mail);
    }

    */


    public void createAppointmentConfirmation(Customer customer, AppointmentDTO appointmentDTO) throws MessagingException {
        Customer customer1 = customerService.getCustomerById(appointmentDTO.getCustomerId());
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(customer.getEmail());
        helper.setSubject("¡Appointment confirmation!");
        String htmlMsg =
                "<html>" +
                        "<head>" +
                        "<style>" +
                        "table {" +
                        "  border-collapse: collapse;" +
                        "  width: 100%;" +
                        "}" +
                        "th, td {" +
                        "  text-align: left;" +
                        "  padding: 8px;" +
                        "}" +
                        "th {" +
                        "  background-color: #dddddd;" +
                        "  color: #333333;" +
                        "}" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<h1 style='color: #007bff;'>Confirmación de reserva</h1>" +
                        "<p>Estimado/a " + customer1.getName() + ",</p>" +
                        "<p>Please, review the details of your reservation in the following table:</p>" +
                        "<table>" +
                        "<tr>" +
                        "<th>Customer</th>" +
                        "<th>appointmentReason</th>" +
                        "<th>appointmentNotes</th>" +
                        "<th>Vet</th>" +
                        "<th>Pet</th>" +
                        "</tr>" +
                        "<tr>" +
                        "<td>" + customer1.getName() + "</td>" +
                        "<td>" + appointmentDTO.getAppointmentReason() + "</td>" +
                        "<td>" + appointmentDTO.getVetId() + "</td>" +
                        "td>" + customer1.getPets().toString() + "</td>" +
                        "<td>" + formattedDateTime + "</td>" +
                        "</tr>" +
                        "</table>" +
                        "<p>Hope to see you soon!.</p>" +
                        "<p>Sincirely,</p>" +
                        "<p>The vet</p>" +
                        "</body>" +
                        "</html>";
        helper.setText(htmlMsg, true);
        javaMailSender.send(message);
    }


    private void sendEmail(Mail mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mail.getSender());
        message.setTo(mail.getRecipient());
        message.setSubject(mail.getSubject());
        message.setText(mail.getContent());

        javaMailSender.send(message);
    }

}

