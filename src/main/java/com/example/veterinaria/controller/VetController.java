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


    @GetMapping("/vetByLicense/{license}")
    public ResponseEntity<Object> findVetByLicense(@PathVariable String license) {
        Optional <Vet> vet = vetService.findByLicense(license);
            return vet.isEmpty()
                    ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vet with license " + license + " doesn't exist")
                    : ResponseEntity.ok(vet);

    }

    @DeleteMapping("/deleteByLicense/{license}")
    public ResponseEntity<String> deleteByLicense(@PathVariable String license){
            vetService.deleteByLicense(license);
            return ResponseEntity.status(HttpStatus.OK).body("Vet with license " + license + " deleted");
    }

    @GetMapping("/findVetByName/{name}")
    public ResponseEntity<Object> findByName(@PathVariable String name){

        Optional<Vet> vetOptional = vetService.findByName(name);

        return vetOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("The name : " + name + " doesnt belong to any Vet on this vet")
                : ResponseEntity.ok(vetOptional);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id){
        Optional<Vet> optionalVet = vetService.getVetById(id);
        return ResponseEntity.ok(optionalVet);

    }

    @DeleteMapping("/deleteByName/{name}")
    public ResponseEntity<String> deleteByName (@PathVariable String name){
        Optional<Vet> optionalVet = vetService.findByName(name);
        if(optionalVet.isPresent()){
            vetService.deleteVet(optionalVet.get().getId());
            return ResponseEntity.status(HttpStatus.OK).body("Vet with name " + name + " deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Theres no vet with the name " + name);
    }

    @DeleteMapping("/byLicense/{license}")
    public ResponseEntity<Object> byLicense (@PathVariable String license){
        Optional<Vet> vetOptional = vetService.findByLicense(license);
        if(vetOptional.isPresent()){
            vetService.deleteByLicense(license);
            return ResponseEntity.ok(vetOptional);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no vet with de license " + license);

    }

}




