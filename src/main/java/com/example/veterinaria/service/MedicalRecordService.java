package com.example.veterinaria.service;

import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.MedicalRecord;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.exception.NotFoundException;
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
    private final AppointmentRepository appointmentRepository;


    public List<MedicalRecord> findAll(){
        return medicalRecordRepository.findAll();
    }

    public Optional <MedicalRecord> findById(Long id) {
        return medicalRecordRepository.findById(id);
    }

    public Optional<MedicalRecord> findByRecordDate(LocalDateTime recordDate){
        return medicalRecordRepository.findByRecordDate(recordDate);
    };

    public MedicalRecord createMR(MedicalRecordDTO medicalRecordDTO) {
        Optional<MedicalRecord> existingRecord = medicalRecordRepository.findByRecordDate(medicalRecordDTO.getRecordDate());
        if (existingRecord.isPresent()) {
            throw new RuntimeException("A MedicalRecord already exists with the same date and time.");
        }

        MedicalRecord medicalRecord = new MedicalRecord();

        medicalRecord.setVaccinationStatus(medicalRecordDTO.getVaccinationStatus());
        medicalRecord.setMedication(medicalRecordDTO.getMedication());
        medicalRecord.setIsNeutered(medicalRecordDTO.getIsNeutered());
        medicalRecord.setAllergies(medicalRecordDTO.getAllergies());
        medicalRecord.setExistingPathologies(medicalRecordDTO.getExistingPathologies());
        medicalRecord.setSurgeries(medicalRecordDTO.getSurgeries());
        medicalRecord.setRecordDate(medicalRecordDTO.getRecordDate());


        medicalRecord.setPet(petService.getPetById(medicalRecordDTO.getPet_id())
                .orElseThrow(() -> new NotFoundException("Pet not found with id: " + medicalRecordDTO.getPet_id())));

        medicalRecord.setVet(vetService.findById(medicalRecordDTO.getVet_id())
                .orElseThrow(() -> new NotFoundException("Vet not found with id: " + medicalRecordDTO.getVet_id())));

        return medicalRecordRepository.save(medicalRecord);
    }




    public void updateMR(MedicalRecordDTO medicalRecordDTO, Long id) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("MedicalRecord not found with id: " + id));

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(medicalRecordDTO, medicalRecord);

        if (medicalRecordDTO.getPet_id() != null && !medicalRecordDTO.getPet_id().equals("")) {
            Optional<Pet> pet = petService.getPetById(medicalRecordDTO.getPet_id());
            pet.ifPresent(medicalRecord::setPet);
        }

        if (medicalRecordDTO.getVet_id() != null && !medicalRecordDTO.getVet_id().equals("")) {
            Optional<Vet> vetOptional = vetService.findById(medicalRecordDTO.getVet_id());
            vetOptional.ifPresent(medicalRecord::setVet);
        }

        medicalRecordRepository.save(medicalRecord);
    }


    public void delete(Long id){
            medicalRecordRepository.deleteById(id);
        }

    public ResponseEntity<?> deleteMedicalRecordByIds(Long[] medicalRecordIds){
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





