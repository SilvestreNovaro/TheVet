package com.example.veterinaria.DTO;


import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Role;
import com.example.veterinaria.validationgroups.CreateValidationGroup;
import com.example.veterinaria.validationgroups.UpdateValidationGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CustomerDTO {


    @Column
    @NotBlank(message = "Name cant be null")
    private String name;
    @Column
    @NotBlank(message = "LastName cant be null")
    private String lastName;
    @Column
    @NotBlank(message = "Address cant be null")
    private String address;
    @Column
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email cant be null")
    private String email;
    @Column
    @NotBlank(message = "Contactnumber cant be null")
    @Min(1)
    private String contactNumber;
    @JsonIgnore
    @Column
    @NotBlank(message = "Password cant be null")
    private String password;
    @Column
    @NotNull
    private LocalDateTime created_at;
    @Column
    private Long roleId;
    @Column
    @NotEmpty(message = "Pets cant be null")
    private List<Pet> pets = new ArrayList<>();

    public CustomerDTO(){
        this.created_at = LocalDateTime.now();
    }

}
