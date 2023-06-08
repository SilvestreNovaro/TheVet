package com.example.veterinaria.repository;

import com.example.veterinaria.entity.Category;
import com.example.veterinaria.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface CategoryRepository extends JpaRepository<Category, Long> {

    public Optional<Category> findByCategoryName(String categoryName);

}
