package com.example.veterinaria.service;

import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.entity.MedicalRecord;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.repository.AppointmentRepository;
import com.example.veterinaria.repository.MedicalRecordRepository;
import com.example.veterinaria.repository.VetRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service

public class MedicalRecordService {


    private final MedicalRecordRepository medicalRecordRepository;

    private final PetService petService;
    private final VetRepository vetService;
    private final CustomerService customerService;

    public List<MedicalRecord> findAll(){
        return medicalRecordRepository.findAll();
    }

    public Optional <MedicalRecord> findById(Long id) {
        return medicalRecordRepository.findById(id);
    }

    public Optional<MedicalRecord> findByRecordDate(LocalDateTime recordDate){
        return medicalRecordRepository.findByRecordDate(recordDate);
    }


    public void updateMR(MedicalRecordDTO medicalRecordDTO, Long id){
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findById(id);
        if(medicalRecordOptional.isPresent()){
            MedicalRecord medicalRecord = medicalRecordOptional.get();

            medicalRecord.setVaccinationStatus(medicalRecordDTO.getVaccinationStatus());
            medicalRecord.setVaccineDates(medicalRecordDTO.getVaccineDates());
            medicalRecord.setMedication(medicalRecordDTO.getMedication());
            medicalRecord.setIsNeutered(medicalRecordDTO.getIsNeutered());
            medicalRecord.setAllergies(medicalRecordDTO.getAllergies());
            medicalRecord.setExistingPathologies(medicalRecordDTO.getExistingPathologies());
            medicalRecord.setSurgeries(medicalRecordDTO.getSurgeries());
            medicalRecord.setRecordDate(medicalRecordDTO.getRecordDate());

            Optional<Vet> vetOptional = vetService.findById(medicalRecordDTO.getVetId());
               vetOptional.ifPresent(medicalRecord::setVet);
            if(vetOptional.isPresent()){
                Vet vet = vetOptional.get();

            }



            medicalRecordRepository.save(medicalRecord);
        }
    }


    public void delete(Long id){
            medicalRecordRepository.deleteById(id);
        }

    public ResponseEntity<String> deleteMedicalRecordByIds(Long[] medicalRecordIds){
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
        if(!deletedIds.isEmpty()){
            return ResponseEntity.ok("The following medical records have been deleted: " + deletedIds);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No medical records for the ids " + notFoundIds);
        }
    }


    }





