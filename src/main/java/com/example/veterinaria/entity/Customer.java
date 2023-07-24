package com.example.veterinaria.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
@Table(name="Customer")
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank
    private String name;
    @Column
    @NotBlank
    private String lastName;
    @Column
    @NotBlank
    private String address;
    @Column
    @NotBlank
    private String email;
    @Column
    @NotNull
    @Min(1)
    private Long contactNumber;

    @Column
    @NotBlank
    private String password;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "customer_id")
    private List<Pet> pets = new ArrayList<>();


    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "role_id")
    private Role role;



}
