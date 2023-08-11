package com.example.veterinaria.service;
import com.example.veterinaria.DTO.VetDTO;
import com.example.veterinaria.entity.Vet;

import com.example.veterinaria.exception.BadRequestException;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.VetRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




import java.util.List;
import java.util.Optional;




@AllArgsConstructor
@Service

public class VetService {

    @Autowired
    private final VetRepository vetRepository;




    public void createVet(Vet vet) {
        vetRepository.findByLicense(vet.getLicense())
        .ifPresent(v -> {
            throw new BadRequestException("License duplicated");
        });
        vetRepository.findByEmail(vet.getEmail())
        .ifPresent(v -> {
            throw new BadRequestException("Email already in use");
        });

        ModelMapper modelMapper = new ModelMapper();
        Vet vetToSave = modelMapper.map(vet, Vet.class);
        vetRepository.save(vetToSave);
    }


   public void updateVet(Vet vet, Long id) {
       Vet existingVet = vetRepository.findById(id)
               .orElseThrow(() -> new NotFoundException("Vet with ID " + id + " not found"));

       vetRepository.findByLicense(vet.getLicense())
               .ifPresent(v -> {
                   throw new BadRequestException("License duplicated");
               });
       vetRepository.findByEmail(vet.getEmail())
               .ifPresent(v -> {
                   throw new BadRequestException("Email already in use");
               });

           ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setPropertyCondition(ctx -> ctx.getSource() != null && !ctx.getSource().equals(""));
           modelMapper.map(vet, existingVet);
           vetRepository.save(existingVet);
       }

    public void updateVetDTO(VetDTO vetDTO, Long id) {
        Vet existingVet = vetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vet with ID " + id + " not found"));

        vetRepository.findByLicense(vetDTO.getLicense())
                .ifPresent(v -> {
                    throw new BadRequestException("License duplicated");
                });
        vetRepository.findByEmail(vetDTO.getEmail())
                .ifPresent(v -> {
                    throw new BadRequestException("Email already in use");
                });

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(vetDTO, existingVet);

        vetRepository.save(existingVet);
    }


    public List<Vet> getAllVets() {
        return vetRepository.findAll();
    }

    public Optional<Vet> getVetById(Long id) {
        return vetRepository.findById(id);
    }

    public void deleteVet(Long id) {
        vetRepository.deleteById(id);
    }

    // add any additional methods here

    public void deleteByLicense(String license){
        vetRepository.deleteByLicense(license);
    }

    public Optional<Vet>findByLicense(String license){
        return vetRepository.findByLicense(license);
    }

    public Optional<Vet>findByName(String name){
        return vetRepository.findByName(name);
    }

    public Optional<Vet> findVetByEmail(String email){
        return vetRepository.findByEmail(email);
    }

    public void deleteBySurName(String surName){
        vetRepository.deleteByName(surName);
    }

    public List<Vet> listOfVetsBySpecialty(String specialty){
        return vetRepository.findBySpecialty(specialty);
    }



}
