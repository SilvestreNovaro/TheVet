package com.example.veterinaria.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.example.veterinaria.DTO.AppointmentDTO;
import com.example.veterinaria.entity.*;
import com.example.veterinaria.service.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Validated
@RestControllerAdvice
@RequestMapping("/appointment")
public class AppointmentController {


    private final AppointmentService appointmentService;

    private final CustomerService customerService;

    private final VetService vetService;

    private final PetService petService;

    private JavaMailSender javaMailSender;






    // GET MAPPING

    @GetMapping("/findById/{id}")
    public ResponseEntity<Object> getAppointmentById(@PathVariable Long id) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
        if (appointment.isPresent()) {
            return ResponseEntity.ok(appointment);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("no appointment found for the id " + id);
        }
    }

    @GetMapping("/findAllAppointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        if (appointments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        }
    }


    @GetMapping("/listOfAppointmentsByDate/{date}")
    public ResponseEntity<Object> findListOfAppointmentsInParticularDate(@PathVariable LocalDate date){
        List<Appointment> appointmentOptional = appointmentService.allAppointmentsByXDate(date);
        if(appointmentOptional.isEmpty()){
            return ResponseEntity.badRequest().body("No se encontraron citas para la fecha y hora especificadas.");
        }
        return ResponseEntity.ok(appointmentOptional);
    }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Object> getAppointmentsByCustomerId(@PathVariable Long customerId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByCustomerId(customerId);
        if(appointments.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("no appointment found for the Customer id " + customerId);
        }
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping("/myAppointments/{customerId}")
    public ResponseEntity<Object> myAppointments(@PathVariable Long customerId){
        Optional<List<Appointment>> appointmentList = Optional.ofNullable(appointmentService.getAppointmentsByCustomerId(customerId));
        return ResponseEntity.status(HttpStatus.OK).body(appointmentList);
    }

    @GetMapping("/vet/{vetId}")
    public ResponseEntity<Object> getAppointmentsByVetId(@PathVariable Long vetId){
        List<Appointment> appointmentList = appointmentService.findByVetId(vetId);
        if(appointmentList.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("no appointment found for the Vet id " + vetId);
        }
        return new ResponseEntity<>(appointmentList, HttpStatus.OK);
    }
    @GetMapping("/pet/{petsId}")
    public ResponseEntity<Object> findByPetId(@PathVariable Long petsId){
        Optional<List<Appointment>> appointmentList = Optional.ofNullable(appointmentService.findByPetsId(petsId));
        if(appointmentList.isPresent()){
            return new ResponseEntity<>(appointmentList, HttpStatus.OK);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No appointment found for the Pet id " + petsId);
        }

    }


    @GetMapping("/vetLicense/{vetLicense}")
    public ResponseEntity<Object> getAppointmentsByLicense(@PathVariable String vetLicense){
        List<Appointment> appointmentList = appointmentService.findByLicense(vetLicense);
        if(appointmentList.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("no appointment found for the Vet license " + vetLicense);
        }
        return new ResponseEntity<>(appointmentList, HttpStatus.OK);
    }


    @GetMapping("/list")
    public List<Appointment> list() {
        return appointmentService.getAllAppointments();
    }



    //CREATE

    @PostMapping("/create")
    public ResponseEntity<Object> createAppointment(@RequestBody AppointmentDTO appointmentDTO) throws MessagingException {

        Optional<Customer> optionalCustomer = customerService.getCustomerById(appointmentDTO.getCustomerId());
        Optional<Vet> optionalVetId = vetService.getVetById(appointmentDTO.getVetId());
        List<Long> petIds = appointmentDTO.getPetIds();
        Optional<Appointment> appointmentOptional = appointmentService.findByAppointmentDateTime(appointmentDTO.getAppointmentDateTime());


        if (optionalCustomer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("customerId " + appointmentDTO.getCustomerId() + " not found");
        }
        if (optionalVetId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("vetId " + appointmentDTO.getVetId() + " not found");
        }
        if (appointmentOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An appointment is already created by the exact same time " + appointmentDTO.getAppointmentDateTime());
        }
        if (appointmentOptional  == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Local date time cant be null");
        }
        if(petIds.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pets found with the ids: " + petIds);
         }

        Customer customer = optionalCustomer.get();

            // Crear el mensaje de correo electrónico
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
                            "<p>Estimado/a " + optionalCustomer.get().getName() + ",</p>" +
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
                            "<td>" + optionalCustomer.get().getName() + "</td>" +
                            "<td>" + appointmentDTO.getAppointmentReason() + "</td>" +
                            "<td>" + optionalVetId.get().getName() + "</td>" +
                            "td>" + optionalCustomer.get().getPets().toString() + "</td>" +
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

        appointmentService.createAppointment(appointmentDTO);
        return new ResponseEntity<>(appointmentDTO, HttpStatus.CREATED);
        }


    //UPDATE (PUT PATCH MAPPING)

    @PutMapping("/updateApp/{id}")
    public ResponseEntity<String> update(@Validated @RequestBody AppointmentDTO appointmentDTO, @PathVariable Long id) {

        Optional<Appointment> appointmentOptional = appointmentService.getAppointmentById(id);
        Optional<Appointment> localDateTimeOptional = appointmentService.findByAppointmentDateTime(appointmentDTO.getAppointmentDateTime());
        if(localDateTimeOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An appointment is already created by the exact same time " + appointmentDTO.getAppointmentDateTime());
        }
        Optional<Customer> optionalCustomer = customerService.findById(appointmentDTO.getCustomerId());
        Optional<Vet> vetOptional = vetService.getVetById(appointmentDTO.getVetId());
        List<Pet> petIds = petService.getAllPetsIds(appointmentDTO.getPetIds());
        if (petIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The petIds " + appointmentDTO.getPetIds() + " cant be null");
        }
        if (optionalCustomer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The CustomerId " + appointmentDTO.getCustomerId() + " doesnt exist");
        }
        if (vetOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The VetId " + appointmentDTO.getVetId() + " doesnt exist");
        }
        if(appointmentOptional.isPresent()) {
            appointmentService.updateAppointmentDTO(appointmentDTO, id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Appointment updated succesfully!!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No appointment found for the id " + id);
    }


    @PatchMapping ("/update/{id}")
    public ResponseEntity<String> updateAppointment(@Validated @RequestBody Appointment appointment, @PathVariable Long id) {
        Optional<Appointment> appointmentOptional = appointmentService.getAppointmentById(id);
        if (appointmentOptional.isPresent()) {
            Optional<Appointment> localDateTimeOptional = appointmentService.findByAppointmentDateTime(appointment.getAppointmentDateTime());
            if (localDateTimeOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An appointment is already created by the exact same time " + appointment.getAppointmentDateTime());
            }
            appointmentService.updateAppointment(id, appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body("Appointment updated succesfully!!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No appointment found for the id " + id);
    }




  //DELETE MAPPING


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) {
        Optional<Appointment> appointmentOptional = appointmentService.getAppointmentById(id);
        if(appointmentOptional.isPresent()){
            appointmentService.deleteAppointmentId(id);
            return ResponseEntity.ok("Appointment with id " + id + " deleted");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No appointment found with the given id " + id);
        }

    }


    //DELETE MANY APPOINTMENTS.
    @DeleteMapping("/deleteByIds")
    public ResponseEntity<Object> deleteAppointmentsByIds(@RequestParam List <Long> appointmentIds) {
        var deletedIds = appointmentService.deleteAppointment(appointmentIds);
        if(deletedIds.isEmpty()){
            return ResponseEntity.ok("non existent ids " + deletedIds);
        }else{
            return ResponseEntity.ok("appointents deleted succesfully " + appointmentIds.toString());
        }

    }


    // ALSO DELETES MANY APPOINTEMTS
    @DeleteMapping("/deleteAppointmentByIds2")
    public ResponseEntity<?> deleteAppointmentsByIds(@RequestParam  Long[] appointmentIds) {
        return appointmentService.deleteAppointmentsByIds(appointmentIds);
    }




}

