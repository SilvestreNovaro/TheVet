package com.example.veterinaria.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @NotNull(message = "Contactnumber cant be null")
    @Min(1)
    private Long contactNumber;
    @Column
    @NotBlank(message = "Password cant be null")
    private String password;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    @NotEmpty(message = "Pets cant be null")
    private List<Pet> pets = new ArrayList<>();


    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "role_id")
    private Role role;

}
