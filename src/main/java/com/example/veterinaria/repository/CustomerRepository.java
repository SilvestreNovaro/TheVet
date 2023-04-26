package com.example.veterinaria.repository;

import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select c from Customer c where c.name = ?1")
    public Optional<Customer> findByName(String name);

    //@Query("select c FROM Pet c where c.pet.id = ?1")
    //@Query("SELECT c FROM Customer c JOIN c.pets p WHERE p.id = ?1")



    @Query("SELECT c.pets FROM Customer c WHERE c.name = :name")
    List<Pet> findPetsByCustomerName(@Param("name") String name);

    @Query("SELECT c FROM Customer c JOIN c.pets p WHERE p.petName = :petName")
    List<Customer> findCustomersByPetName(@Param("petName") String petName);

    public Optional<Customer> findByEmail(String email);


}
