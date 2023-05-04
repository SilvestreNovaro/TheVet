package com.example.veterinaria.DTO;


import com.example.veterinaria.entity.Image;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDTO {



    private String title;

    private String description;

    private Long category_id;

    private List<Image> images;
}
