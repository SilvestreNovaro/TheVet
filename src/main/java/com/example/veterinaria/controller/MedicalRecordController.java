package com.example.veterinaria.controller;

import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.MedicalRecord;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.service.CustomerService;
import com.example.veterinaria.service.MedicalRecordService;
import com.example.veterinaria.service.PetService;
import com.example.veterinaria.service.VetService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RestControllerAdvice
@Validated
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    private final PetService petService;
    private final MedicalRecordService medicalRecordService;
    private final VetService vetService;
    private final CustomerService customerService;


    @GetMapping("/list")
    public List<MedicalRecord> findAll(){
        return medicalRecordService.findAll();
    }


    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@Validated @PathVariable Long id){
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordService.findById(id);
        if(medicalRecordOptional.isPresent()){
             return ResponseEntity.ok(medicalRecordOptional);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No medical record exists with the id " + id);
    }

    @PostMapping("/add")
    public ResponseEntity<?> createMR(@Validated @RequestBody MedicalRecordDTO medicalRecordDTO, @RequestParam Long customerId) {

        Optional<MedicalRecord> medicalRecordOptional = medicalRecordService.findByRecordDate(medicalRecordDTO.getRecordDate());
        if (medicalRecordOptional.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is an exact same date time and hour for a medicalRecord");

        Optional<Vet> vetOptional = vetService.getVetById(medicalRecordDTO.getVet_id());
        if (vetOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("vetId " + medicalRecordDTO.getVet_id() + " not found");
        }
        Optional<Pet> petOptional = petService.getPetById(medicalRecordDTO.getPet_id());
        if (petOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("petId " + medicalRecordDTO.getPet_id() + " not found");
        }
        Optional<Customer> customerOptional = customerService.getCustomerById(customerId);
        if(customerOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("customerId " + customerId + " not found");
        }


        MedicalRecord medicalRecord = medicalRecordService.createMR(medicalRecordDTO, customerId);
        return new ResponseEntity<>(medicalRecord, HttpStatus.CREATED);
    }



    @PutMapping("/modifyDTO/{id}")
    public ResponseEntity<?> updateDTO(@Validated @RequestBody MedicalRecordDTO medicalRecordDTO, @PathVariable Long id, @RequestParam Long customerId){
        Optional<Pet> petOptional = petService.getPetById(medicalRecordDTO.getPet_id());
        if(petOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pet found with the id " + medicalRecordDTO.getPet_id());
        }
        Optional<Vet> vetOptional = vetService.getVetById(medicalRecordDTO.getVet_id());
        if(vetOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No vet found with the id " + medicalRecordDTO.getVet_id());
        }
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordService.findById(id);
        System.out.println("medicalRecordOptional = " + medicalRecordOptional);
        if(medicalRecordOptional.isPresent()){
            medicalRecordService.updateMR(medicalRecordDTO, id, customerId);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No medical record exists with the id " + id);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Medical Record updated successfully!");
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordService.findById(id);
        if(medicalRecordOptional.isPresent()){
            medicalRecordService.delete(id);
            return ResponseEntity.ok("Medical Record with id " + id + " successfully deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Medical Record found with the given id " + id);
    }

    @DeleteMapping("/deleteByIds")
    public ResponseEntity<?> deleteMedicalRecordsByIds(@RequestParam Long[] medicalRecordIds){
        return medicalRecordService.deleteMedicalRecordByIds(medicalRecordIds);
    }

}
