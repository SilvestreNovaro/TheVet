package com.example.veterinaria.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.veterinaria.validationgroups.CreateValidationGroup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Column
    @NotNull(message = "date time cant be null")
    private LocalDateTime appointmentDateTime;

    @Column
    @NotBlank(message = "reason cant be null")
    private String appointmentReason;


    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "customer_id")
    @JsonIgnoreProperties({"pets", "role", "password", "contactNumber", "email", "address"})
    private Customer customer;


    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "vet_id")
    @JsonIgnoreProperties({"email", "license", "image"})
    private Vet vet;


    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL) //LO CAMBIE A DETACH SOLO PARA PODER ELIMINAR APPOINTMENTS, SI NO NO ME DEJABA ELIMNAR XQ PET ESTABA SIENDO REFERENCIADO EN APPOINTMENT_PET, PERO ESTABA EN TYPE.ALL ASI QUE MIRAR QUE COMBIENE.
    @JoinTable(
            name = "appointment_pet",
            joinColumns = @JoinColumn(name = "appointment_id"),
            inverseJoinColumns = @JoinColumn(name = "pet_id")
    )
    @JsonIgnoreProperties("medicalRecords")
    private List<Pet> pets = new ArrayList<>();

}

