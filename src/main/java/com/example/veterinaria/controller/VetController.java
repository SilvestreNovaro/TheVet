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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vet  " + vet.getLicense() + " is already on our registers");
        }
        vetService.createVet(vet);
        return ResponseEntity.status(HttpStatus.CREATED).body("Vet added succesfully!");
    }


    @PutMapping("/modify/{id}")
    public ResponseEntity<?> update(@Validated @RequestBody Vet vet, @PathVariable Long id){
        Optional<Vet> sameLicenseVet = vetService.findByLicense(vet.getLicense());
        Optional<Vet> optionalVet = vetService.getVetById(id);
        if(sameLicenseVet.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vet with the name  " + vet.getLicense() + " is already on our registers");
        }
        if(optionalVet.isPresent()){
            vetService.updateVet(vet, id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Vet updated succesfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vet with the id  " + id + " does not exist on our registers");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete (@PathVariable Long id){
        Optional<Vet> optionalVet = vetService.getVetById(id);
        if(optionalVet.isPresent()){
            vetService.deleteVet(id);
            return ResponseEntity.status(HttpStatus.OK).body("Vet with id " + id + " deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Theres no vet with the id " + id);
    }

    @GetMapping("/vetByLicense/{license}")
    public ResponseEntity<?> findVetByLicense(@PathVariable String license) {
        Optional <Vet> vet = vetService.findByLicense(license);{
            return vet.isEmpty()
                    ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vet with license " + license + " doesnt exist")
                    : ResponseEntity.ok(vet);
        }
    }

    @DeleteMapping("/deleteByLicense/{license}")
    public ResponseEntity<?> deleteByLicense(@PathVariable String license){
        Optional<Vet> optionalVet = vetService.findByLicense(license);
        if(optionalVet.isPresent()){
            vetService.deleteVet(optionalVet.get().getId());
            return ResponseEntity.status(HttpStatus.OK).body("Vet with license " + license + " deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Theres no vet with the license " + license);
    }

    @GetMapping("/findVetByName/{name}")
    public ResponseEntity<?> findByName(@PathVariable String name){

        Optional<Vet> vetOptional = vetService.findByName(name);

        return vetOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("The name : " + name + " doesnt belong to any Vet on this vet")
                : ResponseEntity.ok(vetOptional);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@Validated @PathVariable Long id){
        Optional<Vet> vetOptional = vetService.getVetById(id);
        return vetOptional.isPresent()
                ? ResponseEntity.ok(vetOptional)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("The vet with id : " + id + " doesnt belong to any Vet on this vet");
    }

    @DeleteMapping("/deleteByName/{name}")
    public ResponseEntity<?> deleteByName (@PathVariable String name){
        Optional<Vet> optionalVet = vetService.findByName(name);
        if(optionalVet.isPresent()){
            vetService.deleteVet(optionalVet.get().getId());
            return ResponseEntity.status(HttpStatus.OK).body("Vet with name " + name + " deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Theres no vet with the name " + name);
    }

    @DeleteMapping("/byLicense/{license}")
    public ResponseEntity<?> byLicense (@PathVariable String license){
        Optional<Vet> vetOptional = vetService.findByLicense(license);
        if(vetOptional.isPresent()){
            vetService.deleteByLicense(license);
            return ResponseEntity.ok(vetOptional);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no vet with de license " + license);

    }
    // no logro que ejecute bien la query
}




