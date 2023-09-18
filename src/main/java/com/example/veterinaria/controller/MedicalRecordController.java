package com.example.veterinaria.controller;

import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.entity.MedicalRecord;
import com.example.veterinaria.service.MedicalRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@AllArgsConstructor
@RestController
@RestControllerAdvice
@Validated
@RequestMapping("/medicalRecord")
public class MedicalRecordController {


    private final MedicalRecordService medicalRecordService;



    @GetMapping("/list")
    public List<MedicalRecord> findAll(){
        return medicalRecordService.getAll();
    }


    @GetMapping("/findById/{id}")
    public ResponseEntity<Object> findById(@Validated @PathVariable Long id){
        MedicalRecord medicalRecord = medicalRecordService.findById(id);
             return ResponseEntity.ok(medicalRecord);
    }


    @PatchMapping("/modify/{id}/{customerId}/{petId}")
    public ResponseEntity<String> update(@RequestBody MedicalRecordDTO medicalRecordDTO, @PathVariable Long id, @PathVariable Long customerId, @PathVariable Long petId) {
        medicalRecordService.updateMR(medicalRecordDTO, id, customerId,  petId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Medical Record updated successfully!");
    }




    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
            return ResponseEntity.ok("Medical Record with id " + id + " successfully deleted");
    }

    @DeleteMapping("/deleteByIds")
    public ResponseEntity<String> deleteMedicalRecordsByIds(@RequestParam Long[] medicalRecordIds){
         medicalRecordService.deleteMedicalRecordByIds(medicalRecordIds);
        return ResponseEntity.ok("The following medical records have been deleted: " + Arrays.toString(medicalRecordIds));
    }

    @DeleteMapping("/deleteMrs")
    public ResponseEntity<String> deleteMrsByIds(@RequestParam Long[] medicalRecordIds, Long customerId, Long petId){
        medicalRecordService.deleteMR(medicalRecordIds, customerId, petId);
        return ResponseEntity.ok("Medical Records deleted");
    }

}
