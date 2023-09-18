package com.example.veterinaria.controller;


import com.example.veterinaria.DTO.VaccineDTO;
import com.example.veterinaria.service.VaccineService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RestControllerAdvice
@Validated
@RequestMapping("/vaccine")
public class VaccineController {

    private final VaccineService vaccineService;

    @PatchMapping("/update/{vaccineId}/{customerId}/{petId}")
    public ResponseEntity<String> updateVaccine(@PathVariable Long vaccineId, @PathVariable Long customerId, @PathVariable Long petId, @RequestBody VaccineDTO vaccineDTO){
        vaccineService.update(vaccineId, customerId, petId, vaccineDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Vaccine updated successfully");
    }
}
