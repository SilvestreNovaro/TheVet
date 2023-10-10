package com.example.veterinaria.convert;


import com.example.veterinaria.DTO.CustomerDTO;
import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.MedicalRecord;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.MedicalRecordRepository;
import com.example.veterinaria.service.*;
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

    private final MedicalRecordRepository medicalRecordRepository;


    public CustomerDTO convertCustomerToCustomerDTO(Customer customer){

        return modelMapper.map(customer, CustomerDTO.class);
    }

    public Customer convertCustomerDTOtoCustomerCreate(CustomerDTO customerDTO){
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setLastName(customerDTO.getLastName());
        customer.setPets(customerDTO.getPets());
        customer.setAddress(customerDTO.getAddress());
        customer.setEmail(customerDTO.getEmail());
        customer.setContactNumber(customerDTO.getContactNumber());
        return customer;
    }
    public void createMedicalRecord(Pet pet, MedicalRecordDTO medicalRecordDTO){
        MedicalRecord newMR = new MedicalRecord();
        newMR.setVaccineshot(medicalRecordDTO.getVaccineshot());
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

    public void updateMedicalRecord(MedicalRecordDTO medicalRecordDTO, Long id, Long customerId, Long petId){
        //esta excepcion esta repetida, mirar.
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id).orElseThrow(() -> new NotFoundException("No medical record found"));
        medicalRecord.setVaccineshot(medicalRecordDTO.getVaccineshot());
        medicalRecord.setMedication(medicalRecordDTO.getMedication());
        medicalRecord.setIsNeutered(medicalRecordDTO.getIsNeutered());
        medicalRecord.setAllergies(medicalRecordDTO.getAllergies());
        medicalRecord.setExistingPathologies(medicalRecordDTO.getExistingPathologies());
        medicalRecord.setSurgeries(medicalRecordDTO.getSurgeries());
        medicalRecord.setRecordDate(medicalRecordDTO.getRecordDate());
        Long vetId = medicalRecordDTO.getVetId();
        Vet vet = vetService.getVetById(vetId).orElseThrow(() -> new NotFoundException("Vet not found"));
        medicalRecord.setVet(vet);
        medicalRecordRepository.save(medicalRecord);
    }

    public void updateMedicalRecordt(MedicalRecordDTO medicalRecordDTO, MedicalRecord medicalRecordToUpdate) {
        medicalRecordToUpdate.setMedication(medicalRecordDTO.getMedication());
        medicalRecordToUpdate.setIsNeutered(medicalRecordDTO.getIsNeutered());
        medicalRecordToUpdate.setAllergies(medicalRecordDTO.getAllergies());
        medicalRecordToUpdate.setExistingPathologies(medicalRecordDTO.getExistingPathologies());
        medicalRecordToUpdate.setSurgeries(medicalRecordDTO.getSurgeries());
        medicalRecordToUpdate.setRecordDate(medicalRecordDTO.getRecordDate());

        Long vetId = medicalRecordDTO.getVetId();
        Vet vet = vetService.getVetById(vetId).orElseThrow(() -> new NotFoundException("Vet with id: " + vetId + " not found"));
        medicalRecordToUpdate.setVet(vet);

        medicalRecordRepository.save(medicalRecordToUpdate);
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
        if (StringUtils.isNotBlank(customerDTO.getContactNumber())) {
            existingCustomer.setContactNumber(customerDTO.getContactNumber());
        }
        if (StringUtils.isNotBlank(customerDTO.getPassword())) {
            existingCustomer.setPassword(customerDTO.getPassword());
        }
        return existingCustomer;
    }

    public Customer convertCustomerDTOtoCustomerUpdate1(CustomerDTO customerDTO, Customer existingCustomer) {
        existingCustomer.setName(customerDTO.getName());
        existingCustomer.setLastName(customerDTO.getLastName());
        existingCustomer.setAddress(customerDTO.getAddress());
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setContactNumber(customerDTO.getContactNumber());
        existingCustomer.setPassword(customerDTO.getPassword());
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



}
