package com.example.veterinaria.service;


import com.example.veterinaria.DTO.ProductDTO;
import com.example.veterinaria.entity.Category;
import com.example.veterinaria.entity.Image;
import com.example.veterinaria.entity.Product;
import com.example.veterinaria.exception.NotFoundExceptionLong;
import com.example.veterinaria.repository.ImageRepository;
import com.example.veterinaria.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ProductService {

    private ImageRepository imageRepository;
    private final ProductRepository productRepository;

    private final CategoryService categoryService;


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

        Product product = new Product();



        var productDescription = productDTO.getDescription();
        var productImages = productDTO.getImages();
        var productCategory = productDTO.getCategory_id();
        var productTitle = productDTO.getTitle();


        Optional<Category> categoryOptional = categoryService.findById(productCategory);
        categoryOptional.ifPresent(product::setCategory);

        product.setDescription(productDescription);
        product.setImages(productImages);
        product.setTitle(productTitle);

        return productRepository.save(product);

    }

    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }



    public void agregarImagenAProducto(Long idProducto, Image imagen) {
        // busco el producto por el id
        Product product = productRepository.findById(idProducto).orElseThrow(() -> new NotFoundExceptionLong(idProducto));
        //obtengo la lista actual de imagenes que tenga el producto
        List<Image> imagenes = product.getImages();
        //Si el producto tiene imagenes, se agrega a la lista la nueva, si no tiene imagenes, se crea una lista vacia, y se agrega la imagen en cuestion despues se guarda el producto.
        if(imagenes == null) {
            imagenes = new ArrayList<>();
        }
        imagenes.add(imagen);
        product.setImages(imagenes);
        productRepository.save(product);
    }

    public void deleteImagesFromproduct(Long idProduct, List<Long> imagesIds) {
        // busco el producto por id
        Product product = productRepository.findById(idProduct).orElseThrow(() -> new NotFoundExceptionLong(idProduct));
        // obtengo las imagenes del producto
        List<Image> imagenes = product.getImages();
        //si la lista de imagenes no esta vacia, llamo al metodo removeIf
        if(imagenes != null) {
            //indica que la función toma un parámetro i (que representa un objeto de la clase Image) y devuelve un valor booleano (true o false) según si el ID de la imagen está contenido en la lista de IDs de imágenes que se deben eliminar.
            imagenes.removeIf(i -> imagesIds.contains(i.getId()));
            product.setImages(imagenes);
            productRepository.save(product);
        }
    }



}
