package com.example.veterinaria.repository;

import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select c from Customer c where c.name = ?1")
    public Optional<Customer> findByName(String name);

    //@Query("select c FROM Pet c where c.pet.id = ?1")
    //@Query("SELECT c FROM Customer c JOIN c.pets p WHERE p.id = ?1")
    @Query("SELECT p from Pet p JOIN p.customer c WHERE c.id =?1")
    public List<Pet> getCustomerPets(Long id);


}
