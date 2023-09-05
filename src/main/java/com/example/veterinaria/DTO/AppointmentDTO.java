package com.example.veterinaria.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AppointmentDTO {

    @NotNull(message = "date time cant be null")
    private LocalDateTime appointmentDateTime;

    @NotBlank(message = "reason cant be null")
    private String appointmentReason;

    private Long customerId;

    private Long vetId;

    private List<Long> petIds;




}
