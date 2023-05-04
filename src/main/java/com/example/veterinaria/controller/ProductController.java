package com.example.veterinaria.controller;


import com.example.veterinaria.DTO.ProductDTO;
import com.example.veterinaria.entity.Category;
import com.example.veterinaria.entity.Image;
import com.example.veterinaria.entity.Product;
import com.example.veterinaria.service.CategoryService;
import com.example.veterinaria.service.ProductService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@AllArgsConstructor
@Validated
@RestControllerAdvice
@RequestMapping("/product")
public class ProductController {
    
    private final ProductService productService;

    private final CategoryService categoryService;


    @GetMapping("list")
    public List<Product> list(){
        return productService.findAll();
    }


    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@Validated @RequestBody ProductDTO productDTO) throws MessagingException{
        String title = productDTO.getTitle();
        Optional<Product> productOptional = productService.findByTitle(title);
        if(productOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product with the title " + title + " already exist");
    }
        String description = productDTO.getDescription();
        if(description.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Description cant be null");
        }
        Optional<Category>categoryOptional = categoryService.findById(productDTO.getCategory_id());
        if(categoryOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Theres no Category with the id " + productDTO.getCategory_id() + " on our registers");
        }
        List<Image> images = productDTO.getImages();
        if(images.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Theres are no images");
        }

        Product product = productService.createProduct(productDTO);

        return new ResponseEntity<>(product, HttpStatus.CREATED);




    }





}
