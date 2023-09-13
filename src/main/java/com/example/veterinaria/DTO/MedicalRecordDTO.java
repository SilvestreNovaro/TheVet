package com.example.veterinaria.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class MedicalRecordDTO {

    @NotBlank
    private String vaccineshot;
    @NotBlank
    private String medication;
    @NotNull(message = "esto es en dto")
    private Boolean isNeutered;
    @NotBlank
    private String allergies;
    @NotBlank
    private String existingPathologies;
    @NotBlank
    private String surgeries;
    @NotNull
    private LocalDateTime recordDate;
    @NotNull
    private Long vetId;


    public MedicalRecordDTO() {
        this.recordDate = LocalDateTime.now();
    }


}
