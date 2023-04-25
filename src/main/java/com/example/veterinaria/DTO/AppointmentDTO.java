package com.example.veterinaria.DTO;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentDTO {


    private LocalDateTime appointmentDateTime;

    private String appointmentReason;

    private String appointmentNotes;

    private Long customer_id;

    private Long vet_id;
}
