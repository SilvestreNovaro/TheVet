package com.example.veterinaria.service;

import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.repository.PetRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    public Pet createPet(Pet pet) {
        return petRepository.save(pet);
    }

    public void updatePet(Pet pet, Long id) {
        Optional<Pet> optionalPet = petRepository.findById(id);
        if(optionalPet.isPresent()){
            Pet existingPet = optionalPet.get();
            if(pet.getPetName() !=null && !pet.getPetName().isEmpty()) existingPet.setPetName(pet.getPetName());
            if(pet.getMedicalHistory() !=null && !pet.getMedicalHistory().isEmpty()) existingPet.setMedicalHistory(pet.getMedicalHistory());
            if(pet.getAge() !=null && !pet.getAge().equals("")) existingPet.setAge(pet.getAge());
            if(pet.getGender() !=null && !pet.getGender().isEmpty()) existingPet.setGender(pet.getGender());
            if(pet.getPetSpecies() !=null && !pet.getPetSpecies().isEmpty()) existingPet.setPetSpecies(pet.getPetSpecies());
            petRepository.save(existingPet);
        }


    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public Optional<Pet> getPetById(Long id) {
        return petRepository.findById(id);
    }

    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }

    // add any additional methods here

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
        return petRepository.findBypetSpecies(petSpecies);
    }




}

