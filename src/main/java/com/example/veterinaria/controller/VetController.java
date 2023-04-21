package com.example.veterinaria.controller;


import com.example.veterinaria.entity.Vet;
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
@RequestMapping("/vet")
public class VetController {

    private final VetService vetService;

    @GetMapping("list")
    public List<Vet> list() {
        return vetService.getAllVets();
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Validated @RequestBody Vet vet) {
        Optional<Vet> vetOptional = vetService.findByLicense(vet.getLicense());
        if (vetOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vet  " + vet.getName() + " is already on our registers");
        }
        vetService.createVet(vet);
        return ResponseEntity.status(HttpStatus.CREATED).body("Pet added succesfully!");
    }


    @PutMapping("/modify/{id}")
    public ResponseEntity<?> update(@Validated @RequestBody Vet vet, Long id){
        Optional<Vet> sameLicenseVet = vetService.findByLicense(vet.getLicense());
        Optional<Vet> optionalVet = vetService.getVetById(id);
        if(sameLicenseVet.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vet with the name  " + vet.getName() + " is already on our registers");
        }
        if(optionalVet.isPresent()){
            vetService.updateVet(vet, id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Vet updated succesfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vet with the id  " + id + " does not exist on our registers");
    }

    @DeleteMapping("delete")
    public ResponseEntity<?> delete (@PathVariable Long id){
        Optional<Vet> optionalVet = vetService.getVetById(id);
        if(optionalVet.isPresent()){
            vetService.deleteVet(id);
            return ResponseEntity.status(HttpStatus.OK).body("Pet with id " + id + " deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Theres no vet with the id " + id);
    }
}




