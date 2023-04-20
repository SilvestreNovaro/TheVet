package com.example.veterinaria.repository;

import com.example.veterinaria.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select c from Customers c where c.name = ?1")
    public Optional<Customer> findByName(String name);
}
