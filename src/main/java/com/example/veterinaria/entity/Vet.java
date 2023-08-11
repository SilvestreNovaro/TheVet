package com.example.veterinaria.entity;
import com.example.veterinaria.validationgroups.CreateValidationGroup;
import com.example.veterinaria.validationgroups.UpdateValidationGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

    @NotBlank(groups = {CreateValidationGroup.class, UpdateValidationGroup.class}, message = "Name cant be null")
    private String name;
    @NotBlank(groups = {CreateValidationGroup.class, UpdateValidationGroup.class}, message = "surName cant be null")
    private String surname;
   @Email(message = "Email must be valid")
    @NotBlank(groups = {CreateValidationGroup.class, UpdateValidationGroup.class}, message = "Email cant be null")
    private String email;
    @NotBlank(groups = {CreateValidationGroup.class, UpdateValidationGroup.class}, message = "License cant be null")
    private String license;
    @NotBlank(groups = {CreateValidationGroup.class, UpdateValidationGroup.class}, message = "Image cant be null")
    private String image;
    @NotBlank(groups = {CreateValidationGroup.class, UpdateValidationGroup.class}, message = "Specialty cant be null")
    private String specialty;



}
