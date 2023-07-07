package com.example.veterinaria.controller;

import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.entity.MedicalRecord;
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
    public ResponseEntity<Object> findById(@Validated @PathVariable Long id){
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordService.findById(id);
        if(medicalRecordOptional.isPresent()){
             return ResponseEntity.ok(medicalRecordOptional);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No medical record exists with the id " + id);
    }


    @PutMapping("/modify/{id}")
    public ResponseEntity<String> update(@Validated @RequestBody MedicalRecordDTO medicalRecordDTO, @PathVariable Long id) {
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordService.findById(id);
        if (medicalRecordOptional.isPresent()) {
            if(medicalRecordService.findByRecordDate(medicalRecordDTO.getRecordDate()).isPresent()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Theres already a medical record for the exact same date and time");
            }
            Optional<Vet> vetOptional = vetService.getVetById(medicalRecordDTO.getVetId());
            if (vetOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No vet found with the id " + medicalRecordDTO.getVetId());
            }
            medicalRecordService.updateMR(medicalRecordDTO, id);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No medical record exists with the id " + id);

        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Medical Record updated successfully!");
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordService.findById(id);
        if(medicalRecordOptional.isPresent()){
            medicalRecordService.delete(id);
            return ResponseEntity.ok("Medical Record with id " + id + " successfully deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Medical Record found with the given id " + id);
    }

    @DeleteMapping("/deleteByIds")
    public ResponseEntity<String> deleteMedicalRecordsByIds(@RequestParam Long[] medicalRecordIds){
        return medicalRecordService.deleteMedicalRecordByIds(medicalRecordIds);
    }

}
