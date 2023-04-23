package com.example.veterinaria.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="Pets")
@AllArgsConstructor
@NoArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String petName;
    @Column
    private String medicalHistory;
    @Column
    private Integer age;
    @Column
    private String gender;
    @Column
    private String petSpecies;

    @ManyToOne
    @JoinColumn(name ="customerid")
    private Customer customer;



}
