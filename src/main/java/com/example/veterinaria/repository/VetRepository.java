package com.example.veterinaria.repository;

import com.example.veterinaria.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VetRepository extends JpaRepository <Vet, Long> {

    Optional<Vet> findByLicense(String license);

    Optional<Vet> findByEmail(String email);

    @Query("select v from Vet v where v.name = ?1")
    Optional<Vet> findByName(String name);

    @Query("SELECT v FROM Vet v WHERE v.specialty = :specialty")
    List<Vet> findBySpecialty(@Param("specialty") String specialty);

    void deleteByLicense(String license);

    Optional<Vet> findBySurname(String surname);

    void deleteBySurname(String surname);
}
