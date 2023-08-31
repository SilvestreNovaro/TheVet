package com.example.veterinaria.controller;

import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.entity.MedicalRecord;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.service.CustomerService;
import com.example.veterinaria.service.MedicalRecordService;
import com.example.veterinaria.service.PetService;
import com.example.veterinaria.service.VetService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public ResponseEntity<Object> findById(@Validated @PathVariable Long id){
        MedicalRecord medicalRecord = medicalRecordService.findById(id);
             return ResponseEntity.ok(medicalRecord);
    }


    @PatchMapping("/modify/{id}/{customerId}/{petId}")
    public ResponseEntity<String> update(@RequestBody MedicalRecordDTO medicalRecordDTO, @PathVariable Long id, @PathVariable Long customerId, @PathVariable Long petId) {
        medicalRecordService.updatetoot(medicalRecordDTO, id, customerId,  petId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Medical Record updated successfully!");
    }




    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
            return ResponseEntity.ok("Medical Record with id " + id + " successfully deleted");
    }

    @DeleteMapping("/deleteByIds")
    public ResponseEntity<String> deleteMedicalRecordsByIds(@RequestParam Long[] medicalRecordIds){
        return medicalRecordService.deleteMedicalRecordByIds(medicalRecordIds);
    }

}
