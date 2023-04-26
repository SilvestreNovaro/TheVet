package com.example.veterinaria.DTO;


import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDTO {


    private String name;

    private String lastName;

    private String address;

    private String email;

    private Long contactNumber;

    private String password;

    private Long role_id;


}
