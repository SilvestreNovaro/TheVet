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
import java.util.stream.Collectors;


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
    public List<Product> list() {
        return productService.findAll();
    }


    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@Validated @RequestBody ProductDTO productDTO) throws MessagingException {
        String title = productDTO.getTitle();
        Optional<Product> productOptional = productService.findByTitle(title);
        if (productOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product with the title " + title + " already exist");
        }
        String description = productDTO.getDescription();
        if (description.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Description cant be null");
        }
        Optional<Category> categoryOptional = categoryService.findById(productDTO.getCategory_id());
        if (categoryOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Theres no Category with the id " + productDTO.getCategory_id() + " on our registers");
        }
        List<Image> images = productDTO.getImages();
        if (images.isEmpty()) {
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
    public ResponseEntity<?> addImage(@Validated @RequestBody Image image, @PathVariable Long id) {
        Optional<Product> productOptional = productService.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            productService.addImageToProduct(id, image);
            return ResponseEntity.status(HttpStatus.CREATED).body("image added succesfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given id " + id);
    }

    @PostMapping("/addManyImages/{productId}")
    public ResponseEntity<?> addImagesToProduct(@Validated @PathVariable Long productId, @RequestBody List<Image> images) {
        Optional<Product> productOptional = productService.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            productService.addImagesToProduct(productId, images);
            return ResponseEntity.status(HttpStatus.CREATED).body("images added succesfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given id " + productId);
    }


    @PutMapping("/modifyDTO/{id}")
    public ResponseEntity<?> update(@Validated @RequestBody ProductDTO productDTO, @PathVariable Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        Optional<Product> productOptional = productService.findByTitle(productDTO.getTitle());
        if (productOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The title " + productDTO.getTitle() + " alredy exists on another product");
        }
        Optional<Category> categoryOptional = categoryService.findById(productDTO.getCategory_id());
        if (categoryOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No category for the id " + productDTO.getCategory_id());
        }
        if (optionalProduct.isPresent()) {
            productService.updateProductDTO(productDTO, id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product updated succesfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given id " + id);
    }

    @PatchMapping("/modifyProduct/{id}")
    public ResponseEntity<?> updatxeProduct(@Validated @PathVariable Long id, @RequestBody Product product) {
        Optional<Product> productOptional = productService.findById(id);
        Optional<Product> optionalProduct = productService.findByTitle(product.getTitle());
        if (optionalProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The title " + product.getTitle() + " alredy exists on another product");
        }
        if (productOptional.isPresent()) {
            productService.updateProduct(id, product);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product updated succesfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given id " + id);
    }


    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@Validated @PathVariable Long id) {
        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isPresent()) {
            return ResponseEntity.ok(optionalProduct);
        } else {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found for the id " + id);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@Validated @PathVariable Long id) {
        Optional<Product> productOptional = productService.findById(id);
        if (productOptional.isPresent()) {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Appointment with id " + id + " deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given id " + id);
        }
    }

    @DeleteMapping("/deleteByIds")
    public ResponseEntity<?> deleteProductsByIds(@Validated @RequestParam Long[] productIds) {
        return productService.deleteVariousProductsByIds(productIds);

    }

    @DeleteMapping("/deleteManyProducts")
    public ResponseEntity<Object> deleteProducts(@RequestParam List<Long> productIds) {
        List<Long> deletedIds = productService.deleteProducts(productIds);
        if (deletedIds.size() > 0) {
            return ResponseEntity.ok("non existent ids " + deletedIds);
        } else {
            return ResponseEntity.ok("Products deleted succesfully " + productIds.toString());
        }

    }


    /*@DeleteMapping("/{productId}/images/{imageIds}")
    public ResponseEntity<?> deleteImagesFromProduct(@PathVariable Long productId, @PathVariable List<Long> imageIds) {
        try {
            productService.deleteImagesFromproduct(productId, imageIds);
            return ResponseEntity.ok("Images deleted from product successfully");
        } catch (NotFoundExceptionLong e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

     */


    /*@DeleteMapping("/deleteImages/{productId}/{imageIds}")
    public ResponseEntity<?> deleteImagess(@Validated @PathVariable Long productId, @PathVariable List<Long> imageIds) {
        Optional<Product> productOptional = productService.findById(productId);
        System.out.println("productOptional = " + productOptional);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            List<Image> imageList = product.getImages();
            for (Image imageId : imageList) {
                /*if (imageId.getId().equals(imageIds)) {

                }


                productService.deleteImagesFromproduct(productId, imageIds);
                return ResponseEntity.status(HttpStatus.CREATED).body("Product updated succesfully!");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given id " + productId);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given id " + productId);
    }


     */

    @DeleteMapping("/deleteImages/{productId}/{imageIds}")
    public ResponseEntity<?> deleteImagess(@Validated @PathVariable Long productId, @PathVariable List<Long> imageIds) {
        Optional<Product> productOptional = productService.findById(productId);
        System.out.println("productOptional = " + productOptional);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            List<Image> imageList = product.getImages();

            // Verificar si todos los IDs de imágenes existen en el producto
            //se utiliza para transformar cada elemento del flujo en su respectivo ID. Image::getId es una referencia a método que indica que se utilizará el método getId de la clase Image para realizar esta transformación. En resumen, se extraen los IDs de las imágenes en un nuevo flujo.
            //collect(Collectors.toList()). collect() se utiliza para recolectar los elementos del flujo y almacenarlos en una colección
            //Por último, se utiliza el método containsAll(imageIds) para verificar si todos los IDs de imágenes proporcionados (imageIds) están presentes en la lista de IDs de imágenes del producto. Si todos los IDs de imágenes existen en el producto, se asigna true a la variable allImageIdsExist. Si uno o más IDs de imágenes no existen en el producto, se asigna false.
            boolean allImageIdsExist = imageList.stream()
                    .map(Image::getId)
                    .collect(Collectors.toList())
                    .containsAll(imageIds);

            if (allImageIdsExist) {
                productService.deleteImagesFromproduct(productId, imageIds);
                return ResponseEntity.status(HttpStatus.CREATED).body("Images deleted from product successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("One or more image IDs do not exist in the product");
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given ID " + productId);
    }

}

