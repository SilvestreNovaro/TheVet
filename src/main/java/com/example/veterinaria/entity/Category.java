package com.example.veterinaria.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name="Category")
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private String categoryName;

    private String description;

    @Override
    public String toString() {
        return  categoryName + "";
    }

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Product> products;


}
