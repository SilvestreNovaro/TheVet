package com.example.veterinaria.service;


import com.example.veterinaria.DTO.ProductDTO;
import com.example.veterinaria.entity.Category;
import com.example.veterinaria.entity.Image;
import com.example.veterinaria.entity.Product;
import com.example.veterinaria.exception.NotFoundExceptionLong;
import com.example.veterinaria.repository.ImageRepository;
import com.example.veterinaria.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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

    public void updateProduct(Long id, Product product){

        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isPresent()){
            Product product1 = optionalProduct.get();
            System.out.println("product1 = " + product1);
            System.out.println("product = " + product);
            if(product.getTitle() != null && !product.getTitle().equals("")) product1.setTitle(product.getTitle());
            if(product.getDescription() !=null && !product.equals("")) product1.setDescription(product.getDescription());
            productRepository.save(product1);
        }


    }


    public void updateProductDTO(ProductDTO productDTO, Long id){
        Optional<Product> productOptional = productRepository.findById(id);
        if(productOptional.isPresent()){
            Product product = productOptional.get();
            if(productDTO.getTitle() !=null && !productDTO.getTitle().isEmpty()) product.setTitle(productDTO.getTitle());
            if(productDTO.getDescription() !=null && !productDTO.getDescription().isEmpty()) product.setDescription(productDTO.getDescription());
            if(productDTO.getCategory_id() !=null && !productDTO.getCategory_id().equals("")){
                Long categoryOptional = productDTO.getCategory_id();
                Optional<Category> optionalCategory = categoryService.findById(categoryOptional);
                Category category = optionalCategory.get();
                product.setCategory(category);
            };
            //Creo una lista savedImages que almacenara las instancias de Image guardadas en la base de datos.
            List<Image> savedImages = new ArrayList<>();
            if(productDTO.getImages() !=null && !productDTO.getImages().isEmpty()){
                //Este bucle itera sobra cada instancia de "Image" en la lista propductDTO.getImages()
                for (Image image : productDTO.getImages()) {
                    // Dentro del bucle, cada instancia de Image se guarda en la base de datos utilizando save(image). La instancia guardada se asigna a la variable "savedImage".
                    Image savedImage = imageRepository.save(image);
                    //La instancia guardada de Image se agrega a la lista savedImages, que almacenara las intancias guardadas.
                    savedImages.add(savedImage);
                }

            }
            List<Image> currentImages = product.getImages();
            savedImages.addAll(currentImages);
            product.setImages(savedImages);
            productRepository.save(product);
            //Despues de terminar el bucle, se establece la lista de imagenes guardadas (savedImages) en la entidad product. Esto actualiza la lista de imagenes en la entidad Product con las instancias de Image guardadas en la base de datos.
        }
    }






    public void updatxeProductDTO(ProductDTO productDTO, Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (productDTO.getTitle() != null && !productDTO.getTitle().isEmpty()) {
                product.setTitle(productDTO.getTitle());
            }
            if (productDTO.getDescription() != null && !productDTO.getDescription().isEmpty()) {
                product.setDescription(productDTO.getDescription());
            }
            if (productDTO.getCategory_id() != null && !productDTO.getCategory_id().equals("")) {
                Long categoryOptional = productDTO.getCategory_id();
                Optional<Category> optionalCategory = categoryService.findById(categoryOptional);
                Category category = optionalCategory.get();
                product.setCategory(category);
            }

            // Actualizar la lista de imágenes
            List<Image> savedImages = new ArrayList<>();
            if (productDTO.getImages() != null && !productDTO.getImages().isEmpty()) {
                for (Image image : productDTO.getImages()) {
                    Image savedImage = imageRepository.save(image);
                    savedImages.add(savedImage);
                }
            }

            // Agregar las imágenes existentes en el producto
            List<Image> currentImages = product.getImages();
                savedImages.addAll(currentImages);

            // Establecer la lista de imágenes actualizada en el producto
            product.setImages(savedImages);

            // Guardar el producto actualizado
            productRepository.save(product);
        }
    }





    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }

    public ResponseEntity<?> deleteVariousProductsByIds(Long[] productIds) {
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

    @Transactional
    public List<Long> deleteProducts(List<Long> productIds) {
        List<Long> InexistentIds = new ArrayList<>();

        for (Long productId : productIds) {
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                productRepository.delete(productOptional.get());
            } else {
                InexistentIds.add(productId);
            }
        }

        return InexistentIds;
    }




    public void addImageToProduct(Long productId, Image image) {
        // busco el producto por el id
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundExceptionLong(productId));
        // Guardo la instancia de Image antes de agregarla a la lista
        Image savedImage = imageRepository.save(image);
        //obtengo la lista actual de images que tenga el producto
        List<Image> images = product.getImages();
        //Si el producto tiene images, se agrega a la lista la nueva, si no tiene images, se crea una lista vacia, y se agrega la image en cuestion despues se guarda el producto.
        if(images == null) {
            images = new ArrayList<>();
        }
        images.add(savedImage);
        // Actualizo la lista de imágenes del producto
        product.setImages(images);
        // Guardo el objeto Product, que contiene la lista de imágenes, en el repositorio
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
            System.out.println("product = " + product);
        }
    }



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



}
