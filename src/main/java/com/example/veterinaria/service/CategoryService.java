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


}
