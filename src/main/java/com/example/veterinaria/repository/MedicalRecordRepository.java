package com.example.veterinaria.repository;

import com.example.veterinaria.entity.MedicalRecord;
import com.example.veterinaria.entity.Vet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    Optional<MedicalRecord> findByRecordDate(LocalDateTime recordDate);

    Optional<MedicalRecord> findByRecordDateAndVet(LocalDateTime recordDate, Vet vet);


}
