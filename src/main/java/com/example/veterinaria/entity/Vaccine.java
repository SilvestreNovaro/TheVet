package com.example.veterinaria.entity;

import com.example.veterinaria.validationgroups.CreateValidationGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(groups = {CreateValidationGroup.class}, message = "Name cant be null")
    private String name;

    @Column
    @NotBlank(groups = {CreateValidationGroup.class}, message = "Type cant be null")
    private String type;

    @Column
    @NotNull
    private LocalDateTime dateAdministration;

    @Column
    @NotNull
    private LocalDateTime nextDate;

    @Column
    @NotBlank//(groups = {CreateValidationGroup.class}, message = "Batch cant be null")
    private String batch;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Vet vet;

}
