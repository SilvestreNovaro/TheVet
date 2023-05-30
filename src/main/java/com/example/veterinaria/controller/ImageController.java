package com.example.veterinaria.controller;

import com.example.veterinaria.entity.Image;
import com.example.veterinaria.service.ImageService;
import com.example.veterinaria.service.ProductService;
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
@RequestMapping("/image")
public class ImageController {



    private final ImageService imageService;

    private final ProductService productService;

    @GetMapping("/list")
    public List<Image> list(){
        return imageService.findAll();
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<?> update (@Validated @RequestBody Image image, @PathVariable Long id){
        Optional<Image> imageOptional = imageService.findByUrl(image.getUrl());
        if(imageOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An exact same image is already on use");
        }
        Optional<Image> optionalImage = imageService.findById(id);
        if(optionalImage.isPresent()){
            imageService.updateImage(image, id);
            return ResponseEntity.status(HttpStatus.OK).body("Image " + id + " updated successfully");
        } return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No image found with the id " + id);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){

        Optional<Image> imageOptional = imageService.findById(id);

        return imageOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id: " + id + " doesn't belong to a Product")
                : ResponseEntity.ok(imageOptional);
    }

    @GetMapping("/findByUrl/{url}")
    public ResponseEntity<?> buscarPorId(@PathVariable String url){

        Optional<Image> imageOptional = imageService.findByUrl(url);

        return imageOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id: " + url + " doesn't belong to a Product")
                : ResponseEntity.ok(imageOptional);
    }


}
