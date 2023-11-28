package com.example.veterinaria.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerWithNeuteredPetDTO {
    private Long customerId;
    private String customerName;
    private String customerAddress;
    private String email;
    private String customerContactNumber;
    private Long petId;
    private String petName;

    public CustomerWithNeuteredPetDTO(Long customerId, String customerName, String customerAddress, String email, String customerContactNumber, Long petId, String petName) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.email = email;
        this.customerContactNumber = customerContactNumber;
        this.petId = petId;
        this.petName = petName;
    }
}
