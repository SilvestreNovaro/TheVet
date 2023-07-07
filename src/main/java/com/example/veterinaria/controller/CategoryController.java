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



    // GET REQUESTS

    @GetMapping("list")
    public List<Category> list(){
        return categoryService.findAll();
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Object> findById(@Validated @PathVariable Long id){
        Optional<Category> categoryOptional = categoryService.findById(id);
        if(categoryOptional.isPresent()){
            return ResponseEntity.ok(categoryOptional);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category with the id  " + id + " does not exist on our registers");
    }

    @GetMapping("/findByName/{name}")
    public ResponseEntity<Object> findByName(@Validated @PathVariable String name){
        Optional<Category> categoryOptional = categoryService.findByCategoryName(name);
        if(categoryOptional.isPresent()){
            return ResponseEntity.ok(categoryOptional);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category with the id  " + name + " does not exist on our registers");

    }


    // CREATE REQUEST

    @PostMapping("/add")
    public ResponseEntity<String> add (@Validated @RequestBody Category category){
        Optional<Category> categoryOptional = categoryService.findByCategoryName(category.getCategoryName());
        if(categoryOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("a category with the name " + category.getCategoryName() + " already exists");
        }
        categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category " + category + " added successfully!");

    }

    // UPDATE REQUEST

    @PutMapping("/modify/{id}")
    public ResponseEntity<String> update(@Validated @RequestBody Category category, @PathVariable Long id){
        Optional<Category> optionalCategory = categoryService.findByCategoryName(category.getCategoryName());
        if(optionalCategory.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("a category with the name " + category.getCategoryName() + " already exists");
        }
        Optional<Category> categoryOptional = categoryService.findById(id);
        if(categoryOptional.isPresent()){
            categoryService.updateCategory(category, id);
            return ResponseEntity.status(HttpStatus.OK).body("Category " + category+ " updated!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category " + category + " not found");
    }



    //DELETE MAPPING:

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@Validated @PathVariable Long id){
        Optional<Category> categoryOptional = categoryService.findById(id);
        if(categoryOptional.isPresent()){
            categoryService.deleteCategory(id);
            return ResponseEntity.status(HttpStatus.OK).body("Category " + id + " deleted!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category with the id  " + id + " does not exist on our registers");

    }

    //DELETE PRODUCTS FROM CATEGORY


}
