package com.example.veterinaria.service;

import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.convert.UtilityService;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.MedicalRecord;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.MedicalRecordRepository;
import com.example.veterinaria.repository.PetRepository;
import com.example.veterinaria.repository.VetRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service

public class MedicalRecordService {


    private final MedicalRecordRepository medicalRecordRepository;

    private final PetService petService;

    private final VetRepository vetService;

    private final CustomerService customerService;

    private final UtilityService utilityService;

    private final PetRepository petRepository;

    private static final String NOT_FOUND_MEDICALRECORD = "MedicalRecord not found";

    public List<MedicalRecord> findAll(){
        return medicalRecordRepository.findAll();
    }

    public MedicalRecord findById(Long id) {
        return medicalRecordRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MEDICALRECORD));
    }

    public MedicalRecord findByRecordDate(LocalDateTime recordDate){
        return medicalRecordRepository.findByRecordDate(recordDate).orElseThrow(() -> new NotFoundException(NOT_FOUND_MEDICALRECORD));
    }

    // a elegir con vevis
    /*public void updateMR(MedicalRecordDTO medicalRecordDTO, Long id, Long petId){

        MedicalRecord medicalRecord = medicalRecordRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MEDICALRECORD));
            petService.getPetById(petId);
            utilityService.updateMedicalRecord(medicalRecordDTO, id);
            vetService.findById(medicalRecordDTO.getVetId());
            medicalRecordRepository.save(medicalRecord);
        }

     */



    // a elegir con vevis
    public void updatetoo(MedicalRecordDTO medicalRecordDTO, Long id, Long customerId, Long petId){
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MEDICALRECORD));
        vetService.findById(medicalRecordDTO.getVetId());
        Customer customer = customerService.getCustomerById(customerId);
        List<Pet> petList = customer.getPets();
        for(Pet pets : petList) {
            Pet pet = petList.stream().filter(p -> p.getId().equals(petId)).findFirst().orElseThrow(() -> new NotFoundException("Pet with id: " + petId + " does not belong to the customer"));
            List<MedicalRecord> medicalRecords = pet.getMedicalRecords();
            for(MedicalRecord mr : medicalRecords){
                if(pet.getMedicalRecords().contains(mr))
                    utilityService.updateMedicalRecord(medicalRecordDTO, id, customerId, petId);
            }
            medicalRecordRepository.save(medicalRecord);
        }
    }

    // es el que uso por ahora, que verifica que el medicalrecord sea de la mascota, que la mascota sea del cliente.
    public void updateMR(MedicalRecordDTO medicalRecordDTO, Long id, Long customerId, Long petId) {
        medicalRecordRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MEDICALRECORD));
        Customer customer = customerService.getCustomerById(customerId);
        Pet pet = customer.getPets().stream().filter(p -> p.getId().equals(petId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Pet with id: " + petId + " does not belong to the customer"));

        List<MedicalRecord> medicalRecords = pet.getMedicalRecords();
        Optional<MedicalRecord> foundMedicalRecord = medicalRecords.stream()
                .filter(mr -> mr.getId().equals(id))
                .findFirst();
        if (foundMedicalRecord.isPresent()) {
            utilityService.updateMedicalRecord(medicalRecordDTO, id, customerId, petId);
        } else {
            throw new NotFoundException("Medical Record with id: " + id + " not found for the pet");
        }
    }


    public void deleteMedicalRecordByIds(Long[] medicalRecordIds){
        List<Long> deletedIds = new ArrayList<>();
        List<Long> notFoundIds = new ArrayList<>();
        for(Long medicalRecordId : medicalRecordIds){
            Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findById(medicalRecordId);
            if(medicalRecordOptional.isPresent()){
                medicalRecordRepository.deleteById(medicalRecordId);
                deletedIds.add(medicalRecordId);
            }else{
                notFoundIds.add(medicalRecordId);
            }
        }
        if(!notFoundIds.isEmpty()){
            throw new NotFoundException(NOT_FOUND_MEDICALRECORD + notFoundIds);
        }
    }


    public void deleteMR(Long[] medicalRecordIds, Long customerId, Long petId) {
        Customer customer = customerService.getCustomerById(customerId);
        Pet pet = customer.getPets().stream()
                .filter(p -> p.getId().equals(petId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Pet with id: " + petId + " does not belong to the customer"));

        List<MedicalRecord> medicalRecordList = pet.getMedicalRecords();
        List<Long> validIds = new ArrayList<>();
        List<Long> invalidIds = new ArrayList<>();

        for (Long id : medicalRecordIds) {
            boolean found = medicalRecordList.stream()
                    .anyMatch(mr -> mr.getId().equals(id));
            if (found) {
                validIds.add(id);
            } else {
                invalidIds.add(id);
            }
        }
        if (!invalidIds.isEmpty()) {
            throw new NotFoundException("Medical Record with the following ids not found for the pet: " + invalidIds);
        }
        medicalRecordList.removeIf(mr -> validIds.contains(mr.getId()));
        petRepository.save(pet);
    }


    public void delete(Long id){
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MEDICALRECORD));
            medicalRecordRepository.delete(medicalRecord);
        }





    }





