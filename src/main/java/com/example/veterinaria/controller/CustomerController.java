package com.example.veterinaria.controller;


import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.service.CustomerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RestControllerAdvice
@Validated
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;


    @GetMapping("list")
    public List<Customer> list(){
        return customerService.getAllCustomers();
    }


    @PostMapping("/add")
        public ResponseEntity<?> add (@Validated @RequestBody Customer customer){
        Optional<Customer> customerOptional = customerService.findByName(customer.getName());
        if(customerOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer  " + customer.getName() + " is already on our registers");
        }
        customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body("Customer added succesfully!");
    }


    @PutMapping("/modify/{id}")
    public ResponseEntity<?> update (@Validated @RequestBody Customer customer, Long id){
        Optional<Customer> sameNameCustomer = customerService.findByName(customer.getName());
        Optional<Customer> customerOptional = customerService.getCustomerById(id);
        if(sameNameCustomer.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer with the name  " + customer.getName() + " is already on our registers");
        }
        if(customerOptional.isPresent()){
            customerService.updateCustomer(customer, id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Customert updated succesfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with the id  " + id + " does not exist on our registers");
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Customer> optionalCustomer = customerService.getCustomerById(id);
        if (optionalCustomer.isPresent()) {
            customerService.deleteCustomer(id);
            return ResponseEntity.status(HttpStatus.OK).body("Customer with id " + id + " deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Theres no characteristic with the id " + id);
    }
}
