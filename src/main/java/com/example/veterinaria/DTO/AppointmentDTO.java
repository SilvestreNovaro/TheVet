package com.example.veterinaria.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AppointmentDTO {


    private LocalDateTime appointmentDateTime;

    private String appointmentReason;


    private Long customerId;


    private Long vetId;

    private List<Long> petIds;




}
