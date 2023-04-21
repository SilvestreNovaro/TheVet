package com.example.veterinaria.service;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.repository.VetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VetService {

    @Autowired
    private VetRepository vetRepository;

    public Vet createVet(Vet vet) {
        return vetRepository.save(vet);
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

    public Optional<Vet>findByLicense(String license){
        return vetRepository.findByLicense(license);
    }

}
