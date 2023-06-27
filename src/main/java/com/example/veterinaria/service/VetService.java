package com.example.veterinaria.service;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.repository.VetRepository;
import lombok.AllArgsConstructor;
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
        Vet vet1 = new Vet();
        vet1.setName(vet.getName());
        vet1.setSurname(vet.getSurname());
        vet1.setEmail(vet.getEmail());
        vet1.setLicense(vet.getLicense());
        vet1.setImage(vet.getImage());
        vet1.setSpecialty(vet.getSpecialty());

        vetRepository.save(vet1);
    }

   public void updateVet(Vet vet, Long id) {
       Optional<Vet> optionalVet = vetRepository.findById(id);
       if (optionalVet.isPresent()) {
           Vet existingVet = optionalVet.get();

           ModelMapper modelMapper = new ModelMapper();
           modelMapper.getConfiguration().setPropertyCondition(ctx -> ctx.getSource() != null && !ctx.getSource().equals(""));
           modelMapper.map(vet, existingVet);

           vetRepository.save(existingVet);
       }

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

    public void deleteBySurName(String surName){
        vetRepository.deleteByName(surName);
    }



}
