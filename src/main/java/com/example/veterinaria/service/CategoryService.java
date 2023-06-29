package com.example.veterinaria.service;

import com.example.veterinaria.entity.Category;
import com.example.veterinaria.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
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

    public void createCategory(Category category){
        Category category1 = new Category();
        category1.setCategoryName(category.getCategoryName());
        category1.setDescription(category.getDescription());
        categoryRepository.save(category1);
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







}
