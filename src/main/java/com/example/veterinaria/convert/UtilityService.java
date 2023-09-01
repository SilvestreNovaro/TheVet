package com.example.veterinaria.convert;

import com.example.veterinaria.DTO.CustomerDTO;
import com.example.veterinaria.DTO.MedicalRecordDTO;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.MedicalRecord;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Vet;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.MedicalRecordRepository;
import com.example.veterinaria.service.CustomerService;
import com.example.veterinaria.service.MedicalRecordService;
import com.example.veterinaria.service.PetService;
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

    private final MedicalRecordRepository medicalRecordRepository;

    //private final CustomerService customerService;

   // private final PetService petService;

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
        newMR.setVaccinesJson(medicalRecordDTO.getVaccinesJson());
        Long vetId = medicalRecordDTO.getVetId();
        Vet vet = vetService.getVetById(vetId).get();
        newMR.setVet(vet);
        pet.getMedicalRecords().add(newMR);

    }

    public void updateMedicalRecord(MedicalRecordDTO medicalRecordDTO, Long id, Long customerId, Long petId){
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id).orElseThrow(() -> new NotFoundException("No mr found"));
        medicalRecord.setVaccinationStatus(medicalRecordDTO.getVaccinationStatus());
        medicalRecord.setVaccineDates(medicalRecordDTO.getVaccineDates());
        medicalRecord.setMedication(medicalRecordDTO.getMedication());
        medicalRecord.setIsNeutered(medicalRecordDTO.getIsNeutered());
        medicalRecord.setAllergies(medicalRecordDTO.getAllergies());
        medicalRecord.setExistingPathologies(medicalRecordDTO.getExistingPathologies());
        medicalRecord.setSurgeries(medicalRecordDTO.getSurgeries());
        //medicalRecord.setRecordDate(medicalRecordDTO.getRecordDate());
        medicalRecord.setVaccinesJson(medicalRecordDTO.getVaccinesJson());
        Long vetId = medicalRecordDTO.getVetId();
        Vet vet = vetService.getVetById(vetId).get();
        medicalRecord.setVet(vet);
        medicalRecordRepository.save(medicalRecord);
    }

    public void updateMedicalRecordt(MedicalRecordDTO medicalRecordDTO, MedicalRecord medicalRecordToUpdate) {
        medicalRecordToUpdate.setVaccinationStatus(medicalRecordDTO.getVaccinationStatus());
        medicalRecordToUpdate.setVaccineDates(medicalRecordDTO.getVaccineDates());
        medicalRecordToUpdate.setMedication(medicalRecordDTO.getMedication());
        medicalRecordToUpdate.setIsNeutered(medicalRecordDTO.getIsNeutered());
        medicalRecordToUpdate.setAllergies(medicalRecordDTO.getAllergies());
        medicalRecordToUpdate.setExistingPathologies(medicalRecordDTO.getExistingPathologies());
        medicalRecordToUpdate.setSurgeries(medicalRecordDTO.getSurgeries());
        medicalRecordToUpdate.setRecordDate(medicalRecordDTO.getRecordDate());
        medicalRecordToUpdate.setVaccinesJson(medicalRecordDTO.getVaccinesJson());

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
