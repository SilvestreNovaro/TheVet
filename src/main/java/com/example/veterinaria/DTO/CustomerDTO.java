package com.example.veterinaria.DTO;


import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Role;
import com.example.veterinaria.validationgroups.CreateValidationGroup;
import com.example.veterinaria.validationgroups.UpdateValidationGroup;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomerDTO {


    @Column
    @NotBlank(groups = {CreateValidationGroup.class}, message = "Name cant be null")
    private String name;
    @Column
    @NotBlank(groups = {CreateValidationGroup.class}, message = "LastName cant be null")
    private String lastName;
    @Column
    @NotBlank(groups = {CreateValidationGroup.class}, message = "Address cant be null")
    private String address;
    @Column
    @Email(message = "Email must be valid")
    @NotBlank(groups = {CreateValidationGroup.class}, message = "Email cant be null")
    private String email;
    @Column
    @NotBlank(groups = {CreateValidationGroup.class}, message = "Contactnumber cant be null")
    @Min(1)
    private String contactNumber;
    @Column
    @NotBlank(groups = {CreateValidationGroup.class}, message = "Password cant be null")
    private String password;

    private Long roleId;
    @NotEmpty(message = "Pets cant be null")
    private List<Pet> pets;



}
