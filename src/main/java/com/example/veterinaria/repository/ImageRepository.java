package com.example.veterinaria.repository;

import com.example.veterinaria.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {


    public Optional<Image> findByUrl(String url);


}
