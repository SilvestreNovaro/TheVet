package com.example.veterinaria.controller;



import com.example.veterinaria.DTO.VetDTO;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.service.VetService;
import com.example.veterinaria.validationgroups.CreateValidationGroup;
import com.example.veterinaria.validationgroups.UpdateValidationGroup;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RestControllerAdvice
@Validated
@RequestMapping("/vet")
public class VetController {

    private final VetService vetService;

    @GetMapping("list")
    public List<Vet> list() {
        return vetService.getAllVets();
    }

    @GetMapping("/findBySpecialty/{specialty}")
    public List<Vet> listBySpecialty(@PathVariable String specialty){
        return vetService.listOfVetsBySpecialty(specialty);
    }

    @GetMapping("/findVetByName/{name}")
    public ResponseEntity<Object> findByName(@PathVariable String name){
        Optional<Vet> vetOptional = vetService.findByName(name);
        return ResponseEntity.ok(vetOptional);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id){
        Optional<Vet> optionalVet = vetService.getVetById(id);
        return ResponseEntity.ok(optionalVet);

    }

    @GetMapping("/findByEmail/{email}")
    public ResponseEntity<Object>findVetByEmail(@PathVariable String email){
        Optional<Vet> vetOptional = vetService.findVetByEmail(email);
        return ResponseEntity.ok(vetOptional);
    }

    @GetMapping("/FindVetByLicense/{license}")
    public ResponseEntity<Object> findVetByLicense(@PathVariable String license) {
        Optional<Vet> vetOptional = vetService.findByLicense(license);
        return ResponseEntity.ok(vetOptional);

    }

    @PostMapping("/create")
    public ResponseEntity<String> add(@Validated(CreateValidationGroup.class)@RequestBody Vet vet) {
        vetService.createVet(vet);
        return ResponseEntity.status(HttpStatus.CREATED).body("Vet added successfully!");
    }


    @PatchMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody Vet vet, @PathVariable Long id){
        vetService.updateVet(vet, id);
        return ResponseEntity.status(HttpStatus.CREATED).body("Vet updated successfully!");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete (@PathVariable Long id){
        vetService.deleteVet(id);
        return ResponseEntity.status(HttpStatus.OK).body("Vet with id " + id + " deleted");
    }

    @DeleteMapping("/deleteByLicense/{license}")
    public ResponseEntity<String> deleteByLicense(@PathVariable String license){
            vetService.deleteByLicense(license);
            return ResponseEntity.status(HttpStatus.OK).body("Vet with license " + license + " deleted");
    }

    @DeleteMapping("/deleteBySurName/{surname}")
    public ResponseEntity<String> deleteVetBySurName(@PathVariable String surname){
        vetService.deleteBySurName(surname);
        return ResponseEntity.status(HttpStatus.OK).body("Vet with name " + surname + " deleted");
    }


}




