package com.example.veterinaria.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name="MedicalRecord")
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecord {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String vaccineshot;
    @NotBlank
    private String medication;
    @NotNull(message = "esto es en entidad")
    private Boolean isNeutered;
    @NotBlank
    private String allergies;
    @NotBlank
    private String existingPathologies;
    @NotBlank
    private String surgeries;
    @NotNull
    private LocalDateTime recordDate;


    @JsonIgnoreProperties({"name", "email", "license" })
   @ManyToOne(cascade = CascadeType.DETACH)
   @JoinColumn(name = "vet_id")
   private Vet vet;




}
