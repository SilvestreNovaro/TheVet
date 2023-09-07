package com.example.veterinaria.DTO;

import com.example.veterinaria.validationgroups.CreateValidationGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AppointmentDTO {

    @NotNull(groups = {CreateValidationGroup.class},message = "date time cant be null")
    private LocalDateTime appointmentDateTime;

    @NotBlank(groups = {CreateValidationGroup.class},message = "reason cant be null")
    private String appointmentReason;

    private Long customerId;

    private Long vetId;

    private List<Long> petIds;




}
