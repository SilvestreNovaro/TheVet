package com.example.veterinaria.service;

import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.convert.UtilityService;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.PetRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service

public class PetService {


    private final PetRepository petRepository;

    private final UtilityService utilityService;

    private final VetService vetService;

    public void createPet(Pet pet) {
        petRepository.save(pet);
    }

    public void updatePet(Pet pet, Long id) {
        Pet existingPet = petRepository.findById(id).orElseThrow(() -> new NotFoundException("Pet not found"));
        utilityService.updatePetProperties(existingPet, pet);
            petRepository.save(existingPet);
        }

    public void createMedicalRecord(Long petId, MedicalRecordDTO medicalRecordDTO) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new NotFoundException("Pet not found"));
        vetService.getVetById(medicalRecordDTO.getVetId()).ifPresentOrElse(vet -> {
            utilityService.createMedicalRecord(pet, medicalRecordDTO);
            petRepository.save(pet);
        },
                () -> { throw new NotFoundException("Vet not found");
        });

    }


    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public List<Pet> getAllPetsIds(List<Long> petIds) {
        return petRepository.findAllById(petIds);
    }


    public Optional<Pet> getPetById(Long id) {
        return petRepository.findById(id);
    }

    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }


    public Optional<Pet> findByName(String name) {
        return petRepository.findByName(name);
    }

    public List<Pet> findByAge(Integer age){
        return petRepository.findByAge(age);
    }

    public List<Pet> findByGender(String gender){
        return petRepository.findByGender(gender);
    }

    public List<Pet> findBySpecies(String petSpecies){
        return petRepository.findByPetSpecies(petSpecies);
    }









}

