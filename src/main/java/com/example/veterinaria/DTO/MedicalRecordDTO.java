package com.example.veterinaria.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class MedicalRecordDTO {

    private String vaccinationStatus;

    private LocalDateTime vaccineDates;

    private String medication;

    private Boolean isNeutered;

    private String allergies;

    private String existingPathologies;

    private String surgeries;

    private LocalDateTime recordDate;

    private Long vetId;

    private String vaccinesJson;

    public MedicalRecordDTO() {
        this.recordDate = LocalDateTime.now();
    }


}
