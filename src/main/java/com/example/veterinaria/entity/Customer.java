package com.example.veterinaria.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private String name;
    @Column
    private String lastName;
    @Column
    private String address;
    @Column
    private String email;
    @Column
    private Long contactNumber;

    @Column
    private String password;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="customer_id")
    private List<Pet> pets = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "role_id")
    private Role role;


    /*@ManyToMany
    @JoinTable(name = "customer_vet",
    joinColumns = @JoinColumn(name="customer_id"),
    inverseJoinColumns = @JoinColumn(name = "vet_id"))
    private List<Vet> vets;
*/

}
