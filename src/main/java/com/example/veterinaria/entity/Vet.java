package com.example.veterinaria.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Vet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cant be null")
    private String name;
    @NotBlank(message = "SurName cant be null")
    private String surname;
    @NotBlank(message = "email cant be null")
    private String email;
    @NotBlank(message = "License cant be null")
    private String license;
    @NotBlank(message = "Image cant be null")
    private String image;
    @NotBlank(message = "Specialty cant be null")
    private String specialty;

}
