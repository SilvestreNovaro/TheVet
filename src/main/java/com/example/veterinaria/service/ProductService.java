package com.example.veterinaria.service;


import com.example.veterinaria.DTO.ProductDTO;
import com.example.veterinaria.entity.Category;
import com.example.veterinaria.entity.Image;
import com.example.veterinaria.entity.Product;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.exception.NotFoundExceptionLong;
import com.example.veterinaria.repository.ImageRepository;
import com.example.veterinaria.repository.ProductRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service

public class ProductService {

    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;

    private final CategoryService categoryService;



    // FIND GET METHODs
    public List<Product> findAll(){
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id){
        return productRepository.findById(id);
    }

    public Optional<Product>findByTitle(String title){
        return productRepository.findByTitle(title);
    }


    public Product createProduct(ProductDTO productDTO) {
        String title = productDTO.getTitle();
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }

        String description = productDTO.getDescription();
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }

        Long categoryId = productDTO.getCategoryId();
        Category category = categoryService.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        List<Image> images = productDTO.getImages();
        if (images == null || images.isEmpty()) {
            throw new NotFoundException("Images not found");
        }

        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setImages(images);
        product.setCategory(category);

        return productRepository.save(product);
    }


    public void updateProduct(Long id, Product product) {
        productRepository.findById(id)
                .map(existingProduct -> {
                    if (StringUtils.isNotBlank(product.getTitle())) {
                        existingProduct.setTitle(product.getTitle());
                    } else {
                        throw new IllegalArgumentException("Title is required");
                    }
                    if (StringUtils.isNotBlank(product.getDescription())) {
                        existingProduct.setDescription(product.getDescription());
                    } else {
                        throw new IllegalArgumentException("Description is required");
                    }
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }


    public void updateProductDTO(ProductDTO productDTO, Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            modelMapper.map(productDTO, product);

            productRepository.save(product);
        }
    }


    public void addImageToProduct(Long productId, Image image) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundExceptionLong(productId));

        List<Image> images = product.getImages();
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add(image);
        product.setImages(images);
        productRepository.save(product);
    }


    public void addImagesToProduct(Long productId, List<Image> images){
        Product product = productRepository.findById(productId).get();
        List<Image> imageList = product.getImages();
        List<Image> updatedList = new ArrayList<>();
        for(Image image : images){
            imageRepository.save(image);
            if(image != null){
                updatedList.add(image);
            }
            updatedList.addAll(imageList);
            product.setImages(updatedList);
            productRepository.save(product);
        }
    }

    //DELETE METHODS

    public void deleteImagesFromproduct(Long idProduct, List<Long> imagesIds) {
        // busco el producto por id
        Product product = productRepository.findById(idProduct).orElseThrow(() -> new NotFoundExceptionLong(idProduct));
        // obtengo las imagenes del producto
        List<Image> images = product.getImages();
        //si la lista de imagenes no esta vacia, llamo al metodo removeIf
        if(images != null) {
            //indica que la función toma un parámetro i (que representa un objeto de la clase Image) y devuelve un valor booleano (true o false) según si el ID de la imagen está contenido en la lista de IDs de imágenes que se deben eliminar.
            images.removeIf(i -> imagesIds.contains(i.getId()));
            product.setImages(images);
            productRepository.save(product);
        }
    }

    public void deleteCategoryFromProduct(Long idProduct, Long idCategory){
        Product product = productRepository.findById(idProduct).orElseThrow(() -> new NotFoundExceptionLong(idProduct));
        Category category = product.getCategory();


    }

    @Transactional
    public List<Long> deleteProducts(List<Long> productIds) {
        List<Long> inexistentIds = new ArrayList<>();

        for (Long productId : productIds) {
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                productRepository.delete(productOptional.get());
            } else {
                inexistentIds.add(productId);
            }
        }

        return inexistentIds;
    }

    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }

    public ResponseEntity<String> deleteVariousProductsByIds(Long[] productIds) {
        List<Long> deletedIds = new ArrayList<>();
        List<Long> notFoundIds = new ArrayList<>();

        for (Long productId : productIds) {
            Optional<Product> productOptional = productRepository.findById(productId);
            if(productOptional.isPresent()){
                productRepository.deleteById(productId);
                deletedIds.add(productId);
            } else {
                notFoundIds.add(productId);
            }

        }
        if(!deletedIds.isEmpty()){
            return ResponseEntity.ok("The following products have been deleted " + deletedIds);

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Products not found fot the ids " + notFoundIds);
        }
    }

    public void deleteProductsFromCategory(Long categoryId, List<Long> productIds){

        List<Product> productsToRemove = new ArrayList<>();

        for(Product product : productRepository.findByCategoryId(categoryId)){
            if(productIds.contains(product.getId())){
                productsToRemove.add(product);
            }
        }
            productRepository.deleteAll(productsToRemove);
    }


}
