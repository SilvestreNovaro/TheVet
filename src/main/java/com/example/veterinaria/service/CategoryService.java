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
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;



    public List<Category> findAll(){
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(Long id){
        return categoryRepository.findById(id);
    }


    public Category createCategory(Category category){
        Category category1 = new Category();
        category1.setCategoryName(category.getCategoryName());
        category1.setDescription(category.getDescription());
        return categoryRepository.save(category1);
    }


    public void updateCategory(Category category, Long id){
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if(categoryOptional.isPresent()){
            Category existingCategory = categoryOptional.get();
            if(category.getDescription()!=null && !category.getDescription().isEmpty()) existingCategory.setDescription(category.getDescription());
            if(category.getCategoryName()!=null && !category.getCategoryName().isEmpty()) existingCategory.setCategoryName(category.getCategoryName());
            categoryRepository.save(existingCategory);
        }

    }

    public void deleteCategory(Long id){
        categoryRepository.deleteById(id);
    }

    public Optional<Category> findByCategoryName(String categoryName){
        return categoryRepository.findByCategoryName(categoryName);
    }

    public List<Product> getProductsFromCategory(Long categoryId){
        return categoryRepository.getProductsFromCategory(categoryId);
    }

    /*public void deleteProductsFromCategory(Long categoryId, List<Long> productIds){
        // find the category with id
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundExceptionLong(categoryId));
        System.out.println("CATEGORY: " + category);
        // obtein the products the category has
        List<Product> products = categoryRepository.getProductsFromCategory(categoryId);
        System.out.println("category.getProducts() = " + products.toString());
        //if the category has products then:
        if(products !=null) {
            System.out.println("products en if = " + products);
            //removes the product if the id is a match.

            products.removeIf(p -> productIds.contains(p.getId()) && products.contains(p));


            System.out.println("products.removeIf = " + products);
            // set the new values of products to the category
            category.setProducts(products);
            System.out.println("SetProducts + " + products);
            //save the category
            categoryRepository.save(category);
            System.out.println(category.getProducts());
        }
    }


    /*public void deleteProductsFromCategory(Long categoryId, List<Long> productIds) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundExceptionLong(categoryId));

        List<Product> productsToRemove = new ArrayList<>();

        for (Product product : category.getProducts()) {
            if (productIds.contains(product.getId())) {
                productsToRemove.add(product);
            }
        }

        category.getProducts().removeAll(productsToRemove);
        categoryRepository.save(category);
    }

     */



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





}
