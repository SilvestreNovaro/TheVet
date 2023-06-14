package com.example.veterinaria.DTO;


import com.example.veterinaria.entity.Image;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDTO {



    private String title;

    private String description;
    @NotNull(message = "Category ID cannot be null")
    private Long category_id;
    //@NotNull(message = "Image cannot be null")
    private List<Image> images;

    public void setCategory_Id(@NotNull(message = "Category ID cannot be null") Long category_id) {
        this.category_id = category_id;
    }
}
