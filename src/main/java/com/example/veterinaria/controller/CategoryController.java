package com.example.veterinaria.controller;

import com.example.veterinaria.entity.Category;
import com.example.veterinaria.service.CategoryService;
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
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;


    @GetMapping("list")
    public List<Category> list(){
        return categoryService.findAll();
    }

    @PostMapping("/add")
    public ResponseEntity<?> add (@Validated @RequestBody Category category){
        Optional<Category> categoryOptional = categoryService.findByCategoryName(category.getCategoryName());
        if(categoryOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("a category with the name " + category.getCategoryName() + " already existes");
        }
        categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category " + category + " added succesfully!");

    }

}
