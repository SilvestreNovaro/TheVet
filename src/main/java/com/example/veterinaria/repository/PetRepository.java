package com.example.veterinaria.repository;

import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    @Query("select p from Pet p where p.petName = ?1")
    public Optional<Pet> findByName(String name);


    List<Pet> findByAge(Integer age);

    List<Pet> findByGender(String gender);

    List<Pet> findBypetSpecies(String petSpecies);


}
