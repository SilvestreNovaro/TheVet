package com.example.veterinaria.convert;

import com.example.veterinaria.DTO.VaccineDTO;
import com.example.veterinaria.entity.Vaccine;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.VaccineRepository;
import com.example.veterinaria.service.VetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VaccineUtilityService {

    private final VaccineRepository vaccineRepository;

    private final VetService vetService;

    public void updateVaccine(Long vaccineId, Long customerId, Long petId, VaccineDTO vaccineDTO){
        Vaccine vaccine = vaccineRepository.findById(vaccineId).orElseThrow(() -> new NotFoundException("No vaccine found"));
        vaccine.setType(vaccineDTO.getType());
        vaccine.setBatch(vaccineDTO.getBatch());
        vaccine.setName(vaccineDTO.getName());
        Vet vet = vetService.getVetById(vaccineDTO.getVetId()).orElseThrow(() -> new NotFoundException("Vet not found"));
        vaccine.setVet(vet);
        vaccineRepository.save(vaccine);
    }
}
