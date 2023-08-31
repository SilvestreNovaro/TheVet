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


    private static final String NOT_FOUND_PET = "Pet not found";

    public void createPet(Pet pet) {
        petRepository.save(pet);
    }

    public void updatePet(Pet pet, Long id) {
        Pet existingPet = petRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_PET));
        utilityService.updatePetProperties(existingPet, pet);
        petRepository.save(existingPet);
    }

    public void createMedicalRecord(Long petId, MedicalRecordDTO medicalRecordDTO) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new NotFoundException(NOT_FOUND_PET));
        vetService.getVetById(medicalRecordDTO.getVetId()).ifPresentOrElse(vet -> {
                    utilityService.createMedicalRecord(pet, medicalRecordDTO);
                    petRepository.save(pet);
                },
                () -> {
                    throw new NotFoundException("Vet not found");
                });

    }

    public void deletePet(Long id) {
        petRepository.findById(id).ifPresentOrElse(pet -> petRepository.deleteById(id),
                () -> {
                    throw new NotFoundException(NOT_FOUND_PET);
                });

    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }


    public Pet getPetById(Long id){
        return petRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_PET));
    }

    public Optional<Pet> id(Long id){
        return petRepository.findById(id).or(() -> {
            throw new NotFoundException("nf");
        });
    }




    public List<Pet> findByName(String name) {
        List<Pet> pets = petRepository.findByName(name);
        if (pets.isEmpty()) {
            throw new NotFoundException(NOT_FOUND_PET);
        }
        return pets;
    }

    public List<Pet> findByAge(Integer age) {
        List<Pet> pets = petRepository.findByAge(age);
        if (pets.isEmpty()) {
            throw new NotFoundException(NOT_FOUND_PET);
        }
        return pets;
    }

    public List<Pet> findByGender(String gender) {
        List<Pet> pets = petRepository.findByGender(gender);
        if (pets.isEmpty()) {
            throw new NotFoundException(NOT_FOUND_PET);
        }
        return pets;

    }

    public List<Pet> findBySpecies(String petSpecies) {
        List<Pet> pets = petRepository.findByPetSpecies(petSpecies);
        if (pets.isEmpty()) {
            throw new NotFoundException(NOT_FOUND_PET);
        }
        return pets;
    }


}







