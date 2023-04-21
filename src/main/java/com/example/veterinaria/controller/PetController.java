package com.example.veterinaria.controller;

import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.service.PetService;
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
@RequestMapping("/pet")
public class PetController {

    private final PetService petService;


    @GetMapping("/list")
    public List<Pet> list(){
        return petService.getAllPets();
    }

    @PostMapping("/add")
    public ResponseEntity<?> add (@Validated @RequestBody Pet pet){
        Optional<Pet> petOptional = petService.findByName(pet.getPetName());
        if(petOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pet  " + pet.getPetName() + " is already on our registers");
        }
        petService.createPet(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body("Pet added succesfully!");
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<?> update (@Validated @RequestBody Pet pet, Long id){
        Optional<Pet> sameNamePet= petService.findByName(pet.getPetName());
        Optional<Pet> optionalPet = petService.getPetById(id);
        if(sameNamePet.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pet with the name  " + pet.getPetName() + " is already on our registers");
        }
        if(optionalPet.isPresent()){
            petService.updatePet(pet, id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Pet updated succesfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pet with the id  " + id + " does not exist on our registers");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Optional<Pet> petOptional = petService.getPetById(id);
        if(petOptional.isPresent()){
            petService.deletePet(id);
            return ResponseEntity.status(HttpStatus.OK).body("Pet with id " + id + " deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Theres no pet with the id " + id);
    }
}
