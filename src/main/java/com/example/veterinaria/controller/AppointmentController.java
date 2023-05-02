package com.example.veterinaria.controller;

import java.util.List;
import java.util.Optional;

import com.example.veterinaria.DTO.AppointmentDTO;
import com.example.veterinaria.entity.Appointment;
import com.example.veterinaria.service.AppointmentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Validated
@RestControllerAdvice
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private final AppointmentService appointmentService;

    @GetMapping("/list")
    public List<Appointment> list(){
        return appointmentService.getAllAppointments();
    }

    @PostMapping("/create")
    public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        Appointment savedAppointment = appointmentService.createAppointment(appointmentDTO);
        return new ResponseEntity<>(savedAppointment, HttpStatus.CREATED);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody AppointmentDTO appointmentDTO, @PathVariable Long id){
        Optional<Appointment> optionalAppointment = appointmentService.getAppointmentById(id);
        if(optionalAppointment.isPresent()){
            appointmentService.updateAppointment(appointmentDTO, id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Appointment updated succesfully!");

        }


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No appointment for the id " + id);

    }


    @GetMapping("/findById/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
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
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getAppointmentsByCustomerId(@PathVariable Long customerId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByCustomerId(customerId);
        if(appointments.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("no appointment found for the Customer id " + customerId);
        }
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping("/vet/{vetId}")
    public ResponseEntity<?> getAppointmentsByVetId(@PathVariable Long vetId){
        List<Appointment> appointmentList = appointmentService.findByVetId(vetId);
        if(appointmentList.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("no appointment found for the Vet id " + vetId);
        }
        return new ResponseEntity<>(appointmentList, HttpStatus.OK);
    }

    @GetMapping("/vetLicense/{vetLicense}")
    public ResponseEntity<?> getAppointmentsByLicense(@PathVariable String vetLicense){
        List<Appointment> appointmentList = appointmentService.findByLicense(vetLicense);
        if(appointmentList.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("no appointment found for the Vet license " + vetLicense);
        }
        return new ResponseEntity<>(appointmentList, HttpStatus.OK);
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/deleteByIds")
    public ResponseEntity<Object> deleteAppointmentsByIds(@RequestParam List <Long> appointmentIds) {
        var deletedIds = appointmentService.eliminarCitas(appointmentIds);
        if(deletedIds.size()>0){
            return ResponseEntity.ok("ids inexistentes " + deletedIds);
        }else{
            return ResponseEntity.ok("se eliminaron todos de forma correcta " + appointmentIds.toString());
        }

    }



}

