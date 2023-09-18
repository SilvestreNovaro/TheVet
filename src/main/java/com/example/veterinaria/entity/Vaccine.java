package com.example.veterinaria.entity;

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

    @NotBlank
    private String name;
    @NotBlank
    private String type;
    @NotNull
    private LocalDateTime dateAdministration;
    @NotNull
    private LocalDateTime nextDate;
    @NotBlank
    private String batch;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Vet vet;

}
