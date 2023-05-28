package com.example.veterinaria.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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

    private String vaccinationStatus;

    private String medication;

    private Boolean isNeutered;

    private String allergies;

    private String existingPathologies;

    private String surgeries;

    private LocalDateTime recordDate;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @JsonProperty("petId")
    public Long getPetId() {
        return pet != null ? pet.getId() : null;
    }
    @JsonIgnoreProperties({"name", "email", "phone", "license" })
   @ManyToOne
   @JoinColumn(name = "vet_id")
   private Vet vet;




    private Long customerId;


}
