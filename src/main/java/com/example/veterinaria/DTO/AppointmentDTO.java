package com.example.veterinaria.DTO;

import com.example.veterinaria.entity.Pet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AppointmentDTO {


    private LocalDateTime appointmentDateTime;

    private String appointmentReason;

    private String appointmentNotes;


    private Long customer_id;


    private Long vet_id;

    private List<Long> petIds;




}
