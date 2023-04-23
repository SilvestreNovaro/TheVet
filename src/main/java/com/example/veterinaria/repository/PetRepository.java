package com.example.veterinaria.repository;

import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    @Query("select p from Pet p where p.petName = ?1")
    public Optional<Pet> findByName(String name);

    //@Query("select c from Customer c JOIN Pet where p.petName= ?1")
    @Query("SELECT p.petName FROM Customer c JOIN Pet p ON c.id = p.id WHERE c.name = ?1")
    public Optional<Customer> findPetOwner(String name);
}
