package com.example.veterinaria.controller;


import com.example.veterinaria.DTO.CustomerDTO;
import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.service.CustomerService;
import com.example.veterinaria.service.MailService;
import com.example.veterinaria.service.RoleService;
import com.example.veterinaria.validationgroups.CreateValidationGroup;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@RestController
@Validated
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;


    private final JavaMailSender javaMailSender;

    private final RoleService roleService;

    private final MailService mailService;

    public static final String DELETE_SUCCESS_MESSAGE = " has been successfully deleted from Customer with id ";

    public static final String NOT_FOUND_MESSAGE = "There is no Customer with the id: ";




    @GetMapping("list")
    public List<Customer> list() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/sortAlphabetically")
    public List<Customer> findAllAsc(){
        return customerService.findAllAsc();
    }

    @GetMapping("/totalCustomers")
    public Long countCustomers(){
        return customerService.countCustomers();
    }


    @GetMapping("/{petSpecies}/count")
    public Long countCustomersByPetSpecies(@PathVariable("petSpecies") String petSpecies) {
        return customerService.countCustomersByPetSpecies(petSpecies);
    }

    @GetMapping("/customersWithSpecie/{petSpecies}")
    public List<Customer> findAllCustomersWithTheSpecie(@PathVariable String petSpecies){
        return customerService.listOfCustomersByPetSpecies(petSpecies);
    }


    @GetMapping("/seniorPets")
    public List<Customer> listCustomersBySeniorPets(){
        return customerService.listCustomersWithSeniorPets();
    }



    @PostMapping("/create")
    public ResponseEntity<String> addCustomer(@Validated(CreateValidationGroup.class) @RequestBody CustomerDTO customerDTO) {
        customerService.createCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Customer added successfully");
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody CustomerDTO customerDTO, @PathVariable Long id) {
        customerService.updateCustomerDTO(customerDTO, id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Customer updated successfully!");
        }


    @GetMapping("/findCustomerById/{id}")
    public ResponseEntity<Object> findCustomerById(@PathVariable Long id){
        Customer customer = customerService.getCustomerById(id);
            return ResponseEntity.ok(customer);
    }

    @GetMapping("/findCustomerByLastName/{lastName}")
    public ResponseEntity<Object> findByLastName(@Validated @PathVariable String lastName){
        Optional<Customer> customerOptional = customerService.findByLastName(lastName);
        if(customerOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No customer found with lastName: " + lastName);
        }
        return ResponseEntity.ok(customerOptional);
    }

    @GetMapping("/findCustomerByLastNameAndAddress")
    public ResponseEntity<Object> findByLastNameAndAddress(@Validated @RequestParam String lastName, @RequestParam String address){
        Optional<Customer> customerOptional = customerService.findByLastNameAndAnddress(lastName, address);
        if(customerOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No customer found with lastName: " + lastName + " and address: " + address);
        }
        return  ResponseEntity.ok(customerOptional);
    }

    @GetMapping("/findRoleCustomers")
    public ResponseEntity<Object> findCustomersByCustomersRole(){
        List<Customer> customers = customerService.listOfCustomersRole();
        if(customers.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no Customers");
        }
        return  ResponseEntity.ok(customers);
    }



    @GetMapping("/findByEmail/{email}")
    ResponseEntity<Object> findByEmail(@Validated @PathVariable String email){
        Optional<Customer> optionalCustomer = customerService.findByEmail(email);
        if(optionalCustomer.isPresent()){
            return ResponseEntity.ok(optionalCustomer);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with the email  " + email + " does not exists on our registers");
    }

    @GetMapping("/petsByCustomersLastName/{lastName}")
    public ResponseEntity<Object> findPetsFromCustomer(@PathVariable String lastName) {
        List<Pet> petList = customerService.findPetsByCustomerName(lastName);
            return petList.isEmpty()
                    ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("The customer with name " + lastName + ", doesn't have any pets")
                    : ResponseEntity.ok(petList);

    }

    @GetMapping("/findPetOwner/{name}")
    public ResponseEntity<Object> findOwner(@PathVariable String name) {
        List<Customer> optionalCustomer = customerService.findCustomersByPetName(name);
        if (optionalCustomer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no Pet associated with that owner");
        }
        return ResponseEntity.ok(optionalCustomer);
    }

    @DeleteMapping("/deleteManyPets/{customerId}")
    public ResponseEntity<String> deletePetsById(@PathVariable  Long customerId, @RequestParam List <Long> petIds){
            customerService.deletePetsById(customerId,petIds);
            return ResponseEntity.ok("Pet with id " + petIds + DELETE_SUCCESS_MESSAGE + customerId);
        }



    @DeleteMapping("/deletePetById/{customerId}/{petId}")
    public ResponseEntity<String> deletePetById(@PathVariable Long customerId,@PathVariable Long petId){
       customerService.deletePetById(customerId, petId);
        return ResponseEntity.ok("Pet with id " + petId + DELETE_SUCCESS_MESSAGE + customerId);
    }



    //DELETE.
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
            customerService.deleteCustomer(id);
            return ResponseEntity.status(HttpStatus.OK).body("Customer with id " + id + " deleted");
    }

    @DeleteMapping("/deleteRoleFromCustomer/{customerId}/{roleId}")
    public ResponseEntity<String> deleteRoleFromCustomer (@PathVariable Long customerId, @PathVariable Long roleId){
        customerService.deleteRoleById(customerId, roleId);
        return ResponseEntity.ok("Role with id " + roleId + DELETE_SUCCESS_MESSAGE + customerId);

    }

    @PostMapping("/addRoleToCustomer/{customerId}/{roleId}")
    public ResponseEntity<String> addRole(@PathVariable Long customerId, @PathVariable Long roleId){
        customerService.addRoleToCustomer(customerId, roleId);
        return ResponseEntity.ok("Customer with id " + customerId + " has now the Role " + roleId.toString());
    }

    @PostMapping("/addPetToCustomer/{customerId}")
    public ResponseEntity<Object> addAnimalToCustomer(@Validated @PathVariable Long customerId, @RequestBody Pet pet) {
        customerService.addAnimalToCustomer(customerId, pet);
        return ResponseEntity.ok("Pet added successfully");
    }

    @PostMapping("/addMultiplePets/{customerId}")
    public ResponseEntity<String> addPets(@PathVariable Long customerId,@RequestBody List<Pet> petIds){
            customerService.addMultiplePetsToCustomer(customerId, petIds);
            return ResponseEntity.ok("Added pets tu customer successfully");
    }


}