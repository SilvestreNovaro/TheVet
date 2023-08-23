package com.example.veterinaria.convert;

import com.example.veterinaria.DTO.CustomerDTO;
import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.MedicalRecord;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.service.VetService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.micrometer.common.util.StringUtils;
@AllArgsConstructor
@Component
public class UtilityService {

    @Autowired
    private ModelMapper modelMapper;

    private final VetService vetService;


    public CustomerDTO convertCustomerToCustomerDTO(Customer customer){

        return modelMapper.map(customer, CustomerDTO.class);
    }

    public Customer convertCustomerDTOtoCustomerCreate(CustomerDTO customerDTO){
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setLastName(customerDTO.getLastName());
        customer.setPets(customerDTO.getPets());
        customer.setPassword(customerDTO.getPassword());
        customer.setAddress(customerDTO.getAddress());
        customer.setEmail(customerDTO.getEmail());
        customer.setContactNumber(customerDTO.getContactNumber());
        return customer;
    }
    public void createMedicalRecord(Pet pet, MedicalRecordDTO medicalRecordDTO){
        MedicalRecord newMR = new MedicalRecord();
        newMR.setVaccinationStatus(medicalRecordDTO.getVaccinationStatus());
        newMR.setVaccineDates(medicalRecordDTO.getVaccineDates());
        newMR.setMedication(medicalRecordDTO.getMedication());
        newMR.setIsNeutered(medicalRecordDTO.getIsNeutered());
        newMR.setAllergies(medicalRecordDTO.getAllergies());
        newMR.setExistingPathologies(medicalRecordDTO.getExistingPathologies());
        newMR.setSurgeries(medicalRecordDTO.getSurgeries());
        newMR.setRecordDate(medicalRecordDTO.getRecordDate());
        Long vetId = medicalRecordDTO.getVetId();
        Vet vet = vetService.getVetById(vetId).get();
        newMR.setVet(vet);
        pet.getMedicalRecords().add(newMR);
    }





    public Customer convertCustomerDTOtoCustomerUpdate(CustomerDTO customerDTO, Customer existingCustomer) {
        if (StringUtils.isNotBlank(customerDTO.getName())) {
            existingCustomer.setName(customerDTO.getName());
        }
        if (StringUtils.isNotBlank(customerDTO.getLastName())) {
            existingCustomer.setLastName(customerDTO.getLastName());
        }
        if (StringUtils.isNotBlank(customerDTO.getAddress())) {
            existingCustomer.setAddress(customerDTO.getAddress());
        }
        if (StringUtils.isNotBlank(customerDTO.getEmail())) {
            existingCustomer.setEmail(customerDTO.getEmail());
        }
        if (customerDTO.getContactNumber() != null) {
            existingCustomer.setContactNumber(customerDTO.getContactNumber());
        }
        if (StringUtils.isNotBlank(customerDTO.getPassword())) {
            existingCustomer.setPassword(customerDTO.getPassword());
        }
        return existingCustomer;
    }

    public void createPet(Customer customer, Pet pet){
        Pet newPet = new Pet();
        newPet.setPetName(pet.getPetName());
        newPet.setAge(pet.getAge());
        newPet.setGender(pet.getGender());
        newPet.setPetSpecies(pet.getPetSpecies());
        customer.getPets().add(newPet);
    }

    public void updatePetProperties(Pet existingPet, Pet pet) {
        modelMapper.map(pet, existingPet);
    }



}
