package com.example.veterinaria.repository;

import com.example.veterinaria.entity.Pet;
import com.example.veterinaria.entity.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarianRepository extends JpaRepository <Veterinarian, Long> {
}
