package com.example.veterinaria.service;

import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.entity.MedicalRecord;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.repository.PetRepository;
import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service

public class PetService {


    private final PetRepository petRepository;



    private final VetService vetService;


    public void createPet(Pet pet) {
        petRepository.save(pet);
    }

    public void updatePet(Pet pet, Long id) {
        Optional<Pet> optionalPet = petRepository.findById(id);
        if(optionalPet.isPresent()){
            Pet existingPet = optionalPet.get();
            if(pet.getPetName() !=null && !pet.getPetName().isEmpty()) existingPet.setPetName(pet.getPetName());
            if(pet.getAge() !=null && !pet.getAge().equals("")) existingPet.setAge(pet.getAge());
            if(pet.getGender() !=null && !pet.getGender().isEmpty()) existingPet.setGender(pet.getGender());
            if(pet.getPetSpecies() !=null && !pet.getPetSpecies().isEmpty()) existingPet.setPetSpecies(pet.getPetSpecies());
            petRepository.save(existingPet);
        }

    }


    public void createMedicalRecord(Long petId, MedicalRecordDTO medicalRecordDTO){
        ModelMapper modelMapper = new ModelMapper();
        MedicalRecord medicalRecord = modelMapper.map(medicalRecordDTO, MedicalRecord.class);
        modelMapper.getConfiguration().setPropertyCondition(ctx -> ctx.getSource() != null && !ctx.getSource().equals(""));
        Optional<Pet> petOptional = petRepository.findById(petId);
        Pet pet = petOptional.get();
        List<MedicalRecord> medicalRecords = pet.getMedicalRecords();
        medicalRecords.add(medicalRecord);
        pet.setMedicalRecords(medicalRecords);
        petRepository.save(pet);
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
        return petRepository.findByPetSpecies(petSpecies);
    }









}

