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
        if (StringUtils.isNotBlank(customerDTO.getContactNumber())) {
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

   /* public String buildAppointmentConfirmationEmail(AppointmentDTO appointmentDTO) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        Customer customer = customerService.getCustomerById(appointmentDTO.getCustomerId());
        Optional<Vet> optionalVet = vetService.getVetById(appointmentDTO.getVetId());
        Vet vet = optionalVet.get();
        String htmlMsg =
                "<html>" +
                        "<head>" +
                        "<style>" +
                        "table {" +
                        "  border-collapse: collapse;" +
                        "  width: 100%;" +
                        "}" +
                        "th, td {" +
                        "  text-align: left;" +
                        "  padding: 8px;" +
                        "}" +
                        "th {" +
                        "  background-color: #dddddd;" +
                        "  color: #333333;" +
                        "}" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<h1 style='color: #007bff;'>Confirmación de reserva</h1>" +
                        "<p>Estimado/a " + customer.getName() + ",</p>" +
                        "<p>Please, review the details of your reservation in the following table:</p>" +
                        "<table>" +
                        "<tr>" +
                        "<th>Customer</th>" +
                        "<th>appointmentReason</th>" +
                        "<th>appointmentNotes</th>" +
                        "<th>Vet</th>" +
                        "<th>Pet</th>" +
                        "</tr>" +
                        "<tr>" +
                        "<td>" + customer.getName() + "</td>" +
                        "<td>" + appointmentDTO.getAppointmentReason() + "</td>" +
                        "<td>" + vet.getName() + "</td>" +
                        "<td>" + customer.getPets().toString() + "</td>" +
                        "<td>" + formattedDateTime + "</td>" +
                        "</tr>" +
                        "</table>" +
                        "<p>Hope to see you soon!.</p>" +
                        "<p>Sincirely,</p>" +
                        "<p>The vet</p>" +
                        "</body>" +
                        "</html>";

        return htmlMsg;
    }

    */



}
