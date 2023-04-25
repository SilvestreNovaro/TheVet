package com.example.veterinaria.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime appointmentDateTime;

    private String appointmentReason;

    private String appointmentNotes;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "customer_id")
    @JsonIgnoreProperties({"address", "email", "contactNumber"})
    private Customer customer;

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "pet_id")
    //private Pet pet;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "vet_id")
    private Vet vet;
}

