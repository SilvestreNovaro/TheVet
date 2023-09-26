package com.example.veterinaria.DTO;

import com.example.veterinaria.validationgroups.CreateValidationGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VaccineDTO {

    @NotBlank(groups = {CreateValidationGroup.class}, message = "Name cant be null")
    private String name;
    @NotBlank(groups = {CreateValidationGroup.class}, message = "Type cant be null")
    private String type;
    @NotNull
    private LocalDateTime dateAdministration;
    @NotNull
    private LocalDateTime nextDate;
    @NotBlank//(groups = {CreateValidationGroup.class}, message = "Batch cant be null")
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
