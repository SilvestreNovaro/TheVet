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

    @Query("select v from Vet v where v.license = ?1")
    Optional<Vet> findByLicense(String license);


    @Modifying
    @Query("DELETE FROM Vet v WHERE v.license = :license")
    void deleteByLicense(@Param("license") String license);

    @Query("select v from Vet v where v.name = ?1")
    Optional<Vet> findByName(String name);

    @Query("DELETE FROM Vet v WHERE v.name=?1")
    void deleteByName(String name);

    @Query("SELECT v FROM Vet v WHERE v.specialty = :specialty")

    List<Vet> findBySpecialty(@Param("specialty") String specialty);



}
