package com.example.veterinaria.controller;


import com.example.veterinaria.entity.Customer;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
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


    @GetMapping("list")
    public List<Customer> list(){
        return customerService.list();
    }

    @PostMapping("/add"){
        public ResponseEntity<?> add (@Valid @RequestBody Customer customer){
            Optional<Customer> customerOptional = customerService.
        }
    }
}
