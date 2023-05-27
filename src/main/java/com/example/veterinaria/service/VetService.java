package com.example.veterinaria.service;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.repository.VetRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service

public class VetService {

    @Autowired
    private final VetRepository vetRepository;

    public Vet createVet(Vet vet) {
        Vet vet1 = new Vet();
        vet1.setName(vet.getName());
        vet1.setSurname(vet.getSurname());
        vet1.setEmail(vet.getEmail());
        vet1.setPhone(vet.getPhone());
        vet1.setLicense(vet.getLicense());

        return vetRepository.save(vet1);
    }

    public void updateVet(Vet vet, Long id) {
        Optional<Vet> optionalVet = vetRepository.findById(id);
        if(optionalVet.isPresent()){
            Vet existingVet = optionalVet.get();
            if(vet.getName() !=null && !vet.getName().isEmpty()) existingVet.setName(vet.getName());
            if(vet.getSurname() !=null && !vet.getSurname().isEmpty()) existingVet.setSurname(vet.getSurname());
            if(vet.getEmail() !=null && !vet.getEmail().isEmpty()) existingVet.setEmail(vet.getEmail());
            if(vet.getPhone() !=null && !vet.getPhone().equals("")) existingVet.setPhone(vet.getPhone());
            if(vet.getLicense() !=null && !vet.getLicense().isEmpty()) existingVet.setLicense(vet.getLicense());
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

    public void deleteByName(String name){
        vetRepository.deleteByName(name);
    }



}
