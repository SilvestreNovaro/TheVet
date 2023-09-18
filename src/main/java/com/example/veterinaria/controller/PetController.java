package com.example.veterinaria.controller;


import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.DTO.VaccineDTO;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Vaccine;
import com.example.veterinaria.service.PetService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/create")
    public ResponseEntity<String> add(@Validated @RequestBody Pet pet){
        petService.createPet(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body("Pet added successfully!");
    }

    @PostMapping("/addMedicalRecord")
    public ResponseEntity<Object> addMedicalRecordToPet(@Validated @RequestBody MedicalRecordDTO medicalRecordDTO, @RequestParam Long petId){
        petService.createMedicalRecord(petId, medicalRecordDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("MR added");
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<String> update (@RequestBody Pet pet, @PathVariable Long id){
            petService.updatePet(pet, id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Pet updated successfully!");
        }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
            petService.deletePet(id);
            return ResponseEntity.status(HttpStatus.OK).body("Pet with id " + id + " deleted");
    }

    @GetMapping("/findPetByName/{name}")
    public ResponseEntity<Object> findByName(@PathVariable String name){
        List<Pet> pets  = petService.findByName(name);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Object> find (@PathVariable Long id){
        Pet pet = petService.getPetById(id);
           return ResponseEntity.ok(pet);
        }



    @GetMapping("/bySpecies/{petSpecies}")
    public ResponseEntity<List<Pet>> getPetsBySpecies(@PathVariable String petSpecies){
        List<Pet> petList = petService.findBySpecies(petSpecies);
        return ResponseEntity.ok(petList);
    }

    @GetMapping("/byAge/{age}")
    public ResponseEntity<List<Pet>> getPetsByAge(@PathVariable Integer age) {
        List<Pet> pets = petService.findByAge(age);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/byGender/{gender}")
    public ResponseEntity<List<Pet>> getPetsByGender(@PathVariable String gender){
        List<Pet> pets = petService.findByGender(gender);
            return ResponseEntity.ok(pets);
        }


    @GetMapping("/vaccines/{id}")
    public ResponseEntity<List<Vaccine>> getPetVaccines(@PathVariable Long id){
      List<Vaccine> vaccines = petService.findVaccines(id);
      return ResponseEntity.ok(vaccines);
    }

    @PostMapping("/addVaccine/{id}")
    public ResponseEntity<String> addVaccineStatus(@PathVariable Long id, @RequestBody VaccineDTO vaccineDTO){
        petService.addVaccine(id, vaccineDTO);
        return ResponseEntity.ok().body("Vaccine added successfully!");
    }

}



