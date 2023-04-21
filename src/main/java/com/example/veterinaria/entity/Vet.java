package com.example.veterinaria.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;

    private String email;

    private Long phone;
    private String license;
    @ManyToMany(mappedBy = "vets")
    private List<Customer> customers;

}
