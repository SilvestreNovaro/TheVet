package com.example.veterinaria.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VaccineDTO {

    @NotBlank
    private String name;
    @NotBlank
    private String type;
    @NotNull
    private LocalDateTime dateAdministration;
    @NotNull
    private LocalDateTime nextDate;
    @NotBlank
    private String batch;
    @NotNull
    private Long vetId;

    public VaccineDTO() {
        this.dateAdministration = LocalDateTime.now();
        calculateNextDate();
    }
    public void calculateNextDate() {
        if (name != null) {
            switch (name.toLowerCase()) {
                case "moderna" -> this.nextDate = dateAdministration.plusMonths(6);
                case "tos" -> this.nextDate = dateAdministration.plusMonths(12);
                case "covid" -> this.nextDate = dateAdministration.plusMonths(16);
                default -> this.nextDate = LocalDateTime.now();

            }
        }
    }



}
