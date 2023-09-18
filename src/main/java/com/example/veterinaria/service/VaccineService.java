package com.example.veterinaria.service;

import com.example.veterinaria.DTO.VaccineDTO;
import com.example.veterinaria.convert.VaccineUtilityService;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Vaccine;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.VaccineRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class VaccineService {

    private final VaccineRepository vaccineRepository;

    private final CustomerService customerService;

    private final VaccineUtilityService vaccineUtilityService;


    public List<Vaccine> getAll(){
        return vaccineRepository.findAll();
    }

    public void update(Long vaccineId, Long customerId, Long petId, VaccineDTO vaccineDTO){
        if(vaccineRepository.existsById(vaccineId)){
            Customer customer = customerService.getCustomerById(customerId);
            Pet pet = customer.getPets().stream().filter(p -> p.getId().equals(petId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Pet not found"));
            List<Vaccine> vaccineList = pet.getVaccines();
            Optional<Vaccine> foundVaccine = vaccineList.stream().filter(vaccine -> vaccine.getId().equals(vaccineId)).findFirst();
            if(foundVaccine.isPresent()){
                vaccineUtilityService.updateVaccine(vaccineId, customerId, petId, vaccineDTO);
            }else {
                throw new NotFoundException("No vaccine found");
            }
        }
    }



}
