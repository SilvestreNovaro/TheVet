package com.example.veterinaria.controller;


import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.service.CustomerService;
import com.example.veterinaria.service.PetService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@AllArgsConstructor
@RestController
@RestControllerAdvice
@Validated
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;

    private final PetService petService;


    @GetMapping("list")
    public List<Customer> list() {
        return customerService.getAllCustomers();
    }


    @PostMapping("/add")
    public ResponseEntity<?> add(@Validated @RequestBody Customer customer) {
        Optional<Customer> customerOptional = customerService.findByEmail(customer.getEmail());
        if (customerOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer  " + customer.getEmail() + " is already on our registers");
        }
        customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body("Customer added succesfully!");
    }

    // si quiero agregar un cliente, deberia buscarlo por email, no por nombre ya que no es un atributo unico, y lo que me interesa a mi es cuando doy de alta un cliente el email no exista en la DB.


    @PutMapping("/modify/{id}")
    public ResponseEntity<?> update(@Validated @RequestBody Customer customer, @PathVariable Long id) {
        Optional<Customer> sameEmailCustomer = customerService.findByEmail(customer.getEmail());
        Optional<Customer> customerOptional = customerService.getCustomerById(id);
        if (sameEmailCustomer.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer with the email  " + customer.getEmail() + " is already on our registers");
        }
        if (customerOptional.isPresent()) {
            customerService.updateCustomer(customer, id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Customer updated succesfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with the id  " + id + " does not exist on our registers");
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Customer> optionalCustomer = customerService.getCustomerById(id);
        if (optionalCustomer.isPresent()) {
            customerService.deleteCustomer(id);
            return ResponseEntity.status(HttpStatus.OK).body("Customer with id " + id + " deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Theres no Customer with the id " + id);
    }


    @PostMapping("/addAnimalToCustomer/{customerId}/animals")
    public ResponseEntity<?> addAnimalToCustomer(@Validated @PathVariable Long customerId, @RequestBody Pet pet) {
        Optional<Customer> customerOptional = customerService.getCustomerById(customerId);
        if (customerOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No customer found with the id " + id);
        }
        var customer = customerOptional.get();
        //customer.addPet(pet);
        customerService.createCustomer(customer);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/petsByCustomerName/{name}")
    public ResponseEntity<?> findPetsFromCustomer(@PathVariable String name) {
        List<Pet> petList = customerService.findPetsByCustomerName(name);
        {
            return petList.isEmpty()
                    ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("The customer with id " + id + ", doesnt have any pets")
                    : ResponseEntity.ok(petList);
        }
    }

    @GetMapping("/findPetOwner/{name}")
    public ResponseEntity<?> findOwner(@PathVariable String name) {
        List<Customer> optionalCustomer = customerService.findCustomersByPetName(name);
        if (optionalCustomer.size() > 0) {
            return ResponseEntity.ok(optionalCustomer);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no Pet associated with that owner");
    }

    @PostMapping("/{customerId}/Addpet")
    public ResponseEntity<?> addPetToCustomer(@PathVariable Long customerId, @RequestBody Pet pet) {
        customerService.addPetToCustomer(customerId, pet);

        return ResponseEntity.ok("Se guardo correctamente");
    }


    @DeleteMapping("/deletePetById/{customerId}/{petId}")
    public ResponseEntity<String> deletePetById(@PathVariable Long customerId,@PathVariable Long petId){
       customerService.deletePetById(customerId, petId);
        return ResponseEntity.ok("Pet with id " + petId + " has been successfully deleted from Customer with id " + customerId);
    }



        }
