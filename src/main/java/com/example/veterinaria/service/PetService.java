package com.example.veterinaria.service;

import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.convert.UtilityServiceCustomerPet;
import com.example.veterinaria.entity.MedicalRecord;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.PetRepository;
import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
@AllArgsConstructor
@Service

public class PetService {


    private final PetRepository petRepository;

    @Autowired
    private ModelMapper modelMapper;

    private UtilityServiceCustomerPet utilityServiceCustomerPet;


    public void createPet(Pet pet) {
        petRepository.save(pet);
    }

    public void updatePet(Pet pet, Long id) {
        Pet existingPet = petRepository.findById(id).orElseThrow(() -> new NotFoundException("Pet not found"));
        utilityServiceCustomerPet.updatePetProperties(existingPet, pet);
            petRepository.save(existingPet);
        }

    public void createMedicalRecord(Long petId, MedicalRecordDTO medicalRecordDTO) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setPropertyCondition(ctx -> ctx.getSource() != null && !ctx.getSource().equals(""));
        mapper.typeMap(MedicalRecordDTO.class, MedicalRecord.class).setProvider(request -> new MedicalRecord());

        MedicalRecord medicalRecord = mapper.map(medicalRecordDTO, MedicalRecord.class);

        Optional<Pet> petOptional = petRepository.findById(petId);
        Pet pet = petOptional.orElseThrow(() -> new NoSuchElementException("Pet not found"));

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

