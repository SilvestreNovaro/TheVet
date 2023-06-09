package com.example.veterinaria.controller;


import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.service.PetService;
import com.example.veterinaria.service.VetService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RestControllerAdvice
@Validated
@RequestMapping("/pet")
public class PetController {

    private final PetService petService;
    private final VetService vetService;


    @GetMapping("/list")
    public List<Pet> list(){
        return petService.getAllPets();
    }

    @PostMapping("/add")
    public ResponseEntity<String> add (@Validated @RequestBody Pet pet){
        Optional<Pet> petOptional = petService.findByName(pet.getPetName());
        if(petOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pet  " + pet.getPetName() + " is already on our registers");
        }
        petService.createPet(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body("Pet added successfully!");
    }


    @PostMapping("/addMedicalRecord")
    public ResponseEntity<Object> addMedicalRecordToPet(@RequestBody MedicalRecordDTO medicalRecordDTO, @RequestParam Long petId){
        Optional<Pet> petOptional = petService.getPetById(petId);
        if(petOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pet  id " + petId + " does not exist on our registers");
        }
        Optional<Vet> vetOptional = vetService.getVetById(medicalRecordDTO.getVetId());
        if(vetOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vet  id " + (medicalRecordDTO.getVetId() + " does not exist on our registers"));
        }
        petService.createMedicalRecord(petId, medicalRecordDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("MR added");
    }



    @PutMapping("/modify/{id}")
    public ResponseEntity<String> update (@Validated @RequestBody Pet pet, @PathVariable Long id){
        Optional<Pet> sameNamePet= petService.findByName(pet.getPetName());
        Optional<Pet> optionalPet = petService.getPetById(id);
        if(sameNamePet.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pet with the name  " + pet.getPetName() + " is already on our registers");
        }
        if(optionalPet.isPresent()){
            petService.updatePet(pet, id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Pet updated successfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pet with the id  " + id + " does not exist on our registers");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        Optional<Pet> petOptional = petService.getPetById(id);
        if(petOptional.isPresent()){
            petService.deletePet(id);
            return ResponseEntity.status(HttpStatus.OK).body("Pet with id " + id + " deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Theres no pet with the id " + id);
    }

    @GetMapping("/findPetByName/{name}")
    public ResponseEntity<Object> findByName(@PathVariable String name){

        Optional<Pet> petOptional= petService.findByName(name);

        return petOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("The name : " + name + " doesn't belong to any Pet on this vet")
                : ResponseEntity.ok(petOptional);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Object> find (@PathVariable Long id){
        Optional<Pet> petOptional = petService.getPetById(id);
        if(petOptional.isPresent()){
           return ResponseEntity.ok(petOptional);
        }
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no Pet with the id " + id + " on our registers");
    }





    @GetMapping("/getSpecies/{petSpecies}")
    public ResponseEntity<Object> getSpecies(@PathVariable String petSpecies) {
        List<Pet> petList = petService.getAllPets();
        List<Pet> petList1 = new ArrayList<>();
        for (Pet pet : petList) {
            if (pet.getPetSpecies() !=null && pet.getPetSpecies().equals(petSpecies)) {
                petList1.add(pet);
                }

        }
        if (petList1.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no " + petSpecies + " on our register");

        }
        return ResponseEntity.ok(petList1);
    }

    @GetMapping("/bySpecies/{petSpecies}")
    public ResponseEntity<List<Pet>> getPetsBySpecies(@PathVariable String petSpecies){
        List<Pet> petList = petService.findBySpecies(petSpecies);
        if(petList.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(petList);
    }
    //THIS LAST 2 METHODS ARE THE SAME.




    @GetMapping("/byAge/{age}")
    public ResponseEntity<List<Pet>> getPetsByAge(@PathVariable Integer age) {
        List<Pet> pets = petService.findByAge(age);
        if (pets.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/byGender/{gender}")
    public ResponseEntity<List<Pet>> getPetsByGender(@PathVariable String gender){
        List<Pet> pets = petService.findByGender(gender);
            if(pets.isEmpty()){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(pets);
        }
    }



