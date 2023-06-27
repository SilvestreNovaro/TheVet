package com.example.veterinaria.service;

import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.entity.MedicalRecord;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.repository.PetRepository;
import lombok.AllArgsConstructor;

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

    public void createMedicalRecord(Long petId, MedicalRecord medicalRecord, Long vetId) {
        Optional<Pet> petOptional = petRepository.findById(petId);
            Pet pet = petOptional.get();
            Optional<Vet> vetOptional = vetService.getVetById(vetId);
                Vet vet = vetOptional.get();
                medicalRecord.setVet(vet);
            // Agregar el registro médico a la lista de registros médicos de la mascota
            List<MedicalRecord> medicalRecords = pet.getMedicalRecords();
            medicalRecords.add(medicalRecord);
            pet.setMedicalRecords(medicalRecords);

            // Guardar los cambios en la base de datos
        petRepository.save(pet);
    }

    public void createMR(Long petId, MedicalRecordDTO medicalRecordDTO){
        MedicalRecord mr = new MedicalRecord();
        mr.setIsNeutered(medicalRecordDTO.getIsNeutered());
        mr.setExistingPathologies(medicalRecordDTO.getExistingPathologies());
        mr.setVaccinationStatus(medicalRecordDTO.getVaccinationStatus());
        mr.setAllergies(medicalRecordDTO.getAllergies());
        mr.setRecordDate(medicalRecordDTO.getRecordDate());
        Optional<Vet> vetOptional = vetService.getVetById(medicalRecordDTO.getVetId());
        vetOptional.ifPresent(mr::setVet);
        Optional<Pet> petOptional = petRepository.findById(petId);
        //petOptional.ifPresent(mr::setPet);



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

