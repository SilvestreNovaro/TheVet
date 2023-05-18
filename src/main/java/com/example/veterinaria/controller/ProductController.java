package com.example.veterinaria.controller;


import com.example.veterinaria.DTO.ProductDTO;
import com.example.veterinaria.entity.Category;
import com.example.veterinaria.entity.Image;
import com.example.veterinaria.entity.Product;
import com.example.veterinaria.exception.NotFoundExceptionLong;
import com.example.veterinaria.repository.ProductRepository;
import com.example.veterinaria.service.CategoryService;
import com.example.veterinaria.service.ImageService;
import com.example.veterinaria.service.ProductService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    private final ImageService imageService;
    private final ProductRepository productRepository;


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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no images");
        }
        // Crea una lista de imágenes vacía que se llenará con las imágenes asociadas al nuevo producto
        List<Image> savedImages = new ArrayList<>();

        // Itera sobre las imágenes proporcionadas en el DTO
        for (Image image : images) {
            // Guarda cada imagen en la base de datos
            Image savedImage = imageService.saveImage(image);
            // Agrega la imagen guardada a la lista de imágenes asociadas al nuevo producto
            savedImages.add(savedImage);
        }

        Product product = productService.createProduct(productDTO);

        return new ResponseEntity<>(product, HttpStatus.CREATED);




    }


    @PostMapping("/addImage/{id}")
    public ResponseEntity<?> addImage(@Validated @RequestBody Image image, @PathVariable Long id){
        Optional<Product> productOptional = productService.findById(id);
        if(productOptional.isPresent()){
            Product product = productOptional.get();
            productService.addImageToProduct(id, image);
            return ResponseEntity.status(HttpStatus.CREATED).body("image added succesfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given id " + id);
    }


    @PutMapping("/modify/{id}")
    public ResponseEntity<?> update(@Validated @RequestBody ProductDTO productDTO, @PathVariable Long id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        Optional<Product> productOptional = productService.findByTitle(productDTO.getTitle());
        if(productOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The title " + productDTO.getTitle() + " alredy exists on another product");
        }
        Optional<Category> categoryOptional = categoryService.findById(productDTO.getCategory_id());
        if(categoryOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No category for the id " + productDTO.getCategory_id());
        }
        if(optionalProduct.isPresent()){
            productService.updateProduct(productDTO, id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product updated succesfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given id " + id);
    }


    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById (@Validated @PathVariable Long id){
        Optional<Product> optionalProduct = productService.findById(id);
        if(optionalProduct.isPresent()){
            return ResponseEntity.ok(optionalProduct);
        } else {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No product found for the id " + id);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete (@Validated @PathVariable Long id){
        Optional<Product> productOptional = productService.findById(id);
        if(productOptional.isPresent()){
            productService.deleteProduct(id);
             return ResponseEntity.ok("Appointment with id " + id + " deleted");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given id " + id);
        }
    }

    @DeleteMapping("/deleteByIds")
    public ResponseEntity<?> deleteProductsByIds(@Validated @RequestParam Long[] productIds){
       return productService.deleteVariousProductsByIds(productIds);

    }

    @DeleteMapping("/deleteManyProducts")
    public ResponseEntity<Object> deleteProducts(@RequestParam List <Long> productIds) {
        List<Long> deletedIds = productService.deleteProducts(productIds);
        if(deletedIds.size()>0){
            return ResponseEntity.ok("non existent ids " + deletedIds);
        }else{
            return ResponseEntity.ok("Products deleted succesfully " + productIds.toString());
        }

    }


    @DeleteMapping("/{productId}/images/{imageIds}")
    public ResponseEntity<?> deleteImagesFromProduct(
            @PathVariable Long productId,
            @PathVariable List<Long> imageIds
    ) {
        try {
            productService.deleteImagesFromproduct(productId, imageIds);
            return ResponseEntity.ok("Images deleted from product successfully");
        } catch (NotFoundExceptionLong e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // DELETE PRODUCT FROM CATEGORY








}
