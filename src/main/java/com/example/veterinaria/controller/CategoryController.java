package com.example.veterinaria.controller;

import com.example.veterinaria.entity.Category;
import com.example.veterinaria.service.CategoryService;
import com.example.veterinaria.service.ProductService;
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
        return ResponseEntity.status(HttpStatus.CREATED).body("Category " + category.toString() + " added succesfully!");

    }


    @PutMapping("/modify/{id}")
    public ResponseEntity<?> update(@Validated @RequestBody Category category, @PathVariable Long id){
        Optional<Category> optionalCategory = categoryService.findByCategoryName(category.getCategoryName());
        if(optionalCategory.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("a category with the name " + category.getCategoryName() + " already existes");

        }
        Optional<Category> categoryOptional = categoryService.findById(id);
        if(categoryOptional.isPresent()){
            categoryService.updateCategory(category, id);
            return ResponseEntity.status(HttpStatus.OK).body("Category " + category.toString() + " updated!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category " + category.toString() + " not found");
    }


    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@Validated @PathVariable Long id){
            Optional<Category> categoryOptional = categoryService.findById(id);
            if(categoryOptional.isPresent()){
                return ResponseEntity.ok(categoryOptional);
            }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category with the id  " + id + " does not exist on our registers");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@Validated @PathVariable Long id){
        Optional<Category> categoryOptional = categoryService.findById(id);
        if(categoryOptional.isPresent()){
            categoryService.deleteCategory(id);
            return ResponseEntity.status(HttpStatus.OK).body("Category " + id + " deleted!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category with the id  " + id + " does not exist on our registers");

    }



    //DELETE PRODUCTS FROM CATEGORY

    @DeleteMapping("/deleteProduct/{categoryId}/{productIds}")
    public ResponseEntity<?> deleteProducts(@Validated @PathVariable Long categoryId, @PathVariable List<Long>productIds){
        Optional<Category> categoryOptional = categoryService.findById(categoryId);
        if(categoryOptional.isPresent()){
            categoryService.deleteProductsFromCategory(categoryId, productIds);
            return ResponseEntity.status(HttpStatus.OK).body("Category " + categoryId + " had the products " + productIds + " deleted");

        }else {

        }return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category with the id  " + categoryId + " does not exist on our registers");


    }

}
