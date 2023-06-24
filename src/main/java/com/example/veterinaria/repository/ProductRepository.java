package com.example.veterinaria.repository;

import com.example.veterinaria.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


    public Optional<Product> findByTitle(String title);

    @Query("select p FROM Product p where p.category.id = ?1")
    public List<Product> findByCategoria_Id(Long idCategoria);


}
