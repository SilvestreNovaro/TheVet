package com.example.veterinaria.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.veterinaria.DTO.AppointmentDTO;
import com.example.veterinaria.convert.UtilityService;
import com.example.veterinaria.entity.*;
import com.example.veterinaria.service.*;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
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

    private final UtilityService utilityService;

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
    @GetMapping("/CustomersWithNextsAppointments")
    public List<Customer> findAllCustomersWithAppointmentsNear(@RequestParam LocalDateTime startDate, LocalDateTime endDate){
        return appointmentService.listOfCustomersWithAppointments(startDate, endDate);
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

    @GetMapping("/findAppointmentsBylastName&Address")
    public ResponseEntity<List<Appointment>> getCustomersAppointmentsByLastNameAndAddress(@RequestParam String lastName, String Address){
        List<Appointment> appointments = appointmentService.getAppointmentByLastNameAndAddress(lastName, Address);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }


    @GetMapping("/listOfAppointmentsByDate/{date}")
    public ResponseEntity<Object> findListOfAppointmentsInParticularDate(@PathVariable LocalDate date){
        List<Appointment> appointmentOptional = appointmentService.allAppointmentsByXDate(date);
        if(appointmentOptional.isEmpty()){
            return ResponseEntity.badRequest().body("No appointments found for the specified date and time.");
        }
        return ResponseEntity.ok(appointmentOptional);
    }

    @GetMapping("/findAppointmentByDateAndTime/{appointmentDateTime}")
    public ResponseEntity<Object> findAppointmentByLocalDateTime(@PathVariable LocalDateTime appointmentDateTime){
        Optional<Appointment> appointmentOptional = appointmentService.findByAppointmentDateTime(appointmentDateTime);
        if(appointmentOptional.isEmpty()){
            return ResponseEntity.badRequest().body("No appointments found for the specified date and time.");
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
    public ResponseEntity<Object> createAppointment(@Validated @RequestBody AppointmentDTO appointmentDTO) throws MessagingException {

        appointmentService.createAppointment(appointmentDTO);
        return new ResponseEntity<>(appointmentDTO, HttpStatus.CREATED);
        }



    //UPDATE (PUT PATCH MAPPING)

  @PatchMapping("/updateApp/{id}")
    public ResponseEntity<String> update(@RequestBody AppointmentDTO appointmentDTO, @PathVariable Long id) {
        appointmentService.updateAppointment(appointmentDTO, id);
        return ResponseEntity.status(HttpStatus.CREATED).body("Appointment updated succesfully!!");
    }


    @PatchMapping ("/update/{id}")
    public ResponseEntity<String> updateAppointment(@RequestBody Appointment appointmentDTO, @PathVariable Long id) {
            appointmentService.updateAppointment(id, appointmentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Appointment updated succesfully!!");
    }


  //DELETE MAPPING

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointmentId(id);
            return ResponseEntity.ok("Appointment with id " + id + " deleted");
    }


    //DELETE MANY APPOINTMENTS.
    @DeleteMapping("/deleteByIds")
    public ResponseEntity<Object> deleteAppointmentsByIds(@RequestParam List <Long> appointmentIds) {
        appointmentService.deleteAppointment(appointmentIds);
        return ResponseEntity.ok("appointents deleted succesfully " + appointmentIds.toString());
    }


    // ALSO DELETES MANY APPOINTEMTS
    @DeleteMapping("/deleteAppointmentByIds2")
    public ResponseEntity<String> deleteAppointmentsByIds(@RequestParam  Long[] appointmentIds) {
        appointmentService.deleteAppointmentsByIds(appointmentIds);
        return ResponseEntity.ok("appointents deleted succesfully " + appointmentIds.toString());
    }




}

