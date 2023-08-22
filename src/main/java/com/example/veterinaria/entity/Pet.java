package com.example.veterinaria.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="Pet")
@AllArgsConstructor
@NoArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @NotBlank(message = "name cant be null")
    private String petName;
    @Column
    @NotNull(message = "age cant be null")
    private Integer age;
    @Column
    @NotBlank(message = "gender cant be null")
    private String gender;
    @Column
    @NotBlank(message = "petSpecies cant be null")
    private String petSpecies;


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pet_id")
    private List<MedicalRecord> medicalRecords = new ArrayList<>();





}

