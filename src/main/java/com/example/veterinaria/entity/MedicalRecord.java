package com.example.veterinaria.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
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

    @NotNull
    private String vaccinationStatus;
    @NotNull
    private String medication;
    @NotNull
    private Boolean isNeutered;
    @NotNull
    private String allergies;
    @NotNull
    private String existingPathologies;
    @NotNull
    private String surgeries;
    @NotNull
    private LocalDateTime recordDate;

    @JsonIgnoreProperties({"name", "email", "phone", "license" })
   @ManyToOne(cascade = CascadeType.DETACH)
   @JoinColumn(name = "vet_id")
   private Vet vet;




}
