package com.example.veterinaria.service;

import com.example.veterinaria.entity.Category;
import com.example.veterinaria.entity.Product;
import com.example.veterinaria.exception.NotFoundExceptionLong;
import com.example.veterinaria.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@Service

public class CategoryService {

    private final CategoryRepository categoryRepository;



    //GET REQUESTS
    public List<Category> findAll(){
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(Long id){
        return categoryRepository.findById(id);
    }

    public Optional<Category> findByCategoryName(String categoryName){
        return categoryRepository.findByCategoryName(categoryName);
    }


    // CREATE

    public Category createCategory(Category category){
        Category category1 = new Category();
        category1.setCategoryName(category.getCategoryName());
        category1.setDescription(category.getDescription());
        return categoryRepository.save(category1);
    }



    //UPDATE

    public void updateCategory(Category category, Long id) {
        categoryRepository.findById(id).ifPresent(existingCategory -> {
            if (category.getDescription() != null && !category.getDescription().isEmpty()) {
                existingCategory.setDescription(category.getDescription());
            }
            if (category.getCategoryName() != null && !category.getCategoryName().isEmpty()) {
                existingCategory.setCategoryName(category.getCategoryName());
            }
            categoryRepository.save(existingCategory);
        });
    }



    // DELETE

    public void deleteCategory(Long id){
        categoryRepository.deleteById(id);
    }



    public void deleteProductsFromCategory(Long categoryId, List<Long> productIds) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundExceptionLong(categoryId));

        List<Product> productsToRemove = new ArrayList<>();

        for (Product product : category.getProducts()) {
            if (productIds.contains(product.getId())) {
                productsToRemove.add(product);
                product.setCategory(null); // Eliminar la referencia de la categoría en el producto
            }
        }

        category.getProducts().removeAll(productsToRemove);
        categoryRepository.save(category);
    }

    public void deleteAllProducts(Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundExceptionLong(categoryId));

        category.getProducts().clear();
        categoryRepository.save(category);
    }





}
