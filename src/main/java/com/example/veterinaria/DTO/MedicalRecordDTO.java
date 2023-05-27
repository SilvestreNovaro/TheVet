package com.example.veterinaria.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MedicalRecordDTO {

    private String vaccinationStatus;

    private String medication;

    private Boolean isNeutered;

    private String allergies;

    private String existingPathologies;

    private String surgeries;

    private LocalDateTime recordDate;

    private Long pet_id;

    //private Long customer_id;

    private Long vet_id;


}
