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
    public ResponseEntity<?> findById(@Validated @PathVariable Long id){
        Optional<Category> categoryOptional = categoryService.findById(id);
        if(categoryOptional.isPresent()){
            return ResponseEntity.ok(categoryOptional);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category with the id  " + id + " does not exist on our registers");
    }

    @GetMapping("/findByName/{name}")
    public ResponseEntity<?> findByName(@Validated @PathVariable String name){
        Optional<Category> categoryOptional = categoryService.findByCategoryName(name);
        if(categoryOptional.isPresent()){
            return ResponseEntity.ok(categoryOptional);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category with the id  " + name + " does not exist on our registers");

    }

    /*
    @GetMapping("/getProductsFromCategory/{id}")
    public ResponseEntity<?> getProductsCategory(@Validated @PathVariable Long id) {
        Optional<Category> categoryOptional = categoryService.findById(id);
        if (categoryOptional.isPresent()) {
            Category category1 = categoryOptional.get();
            List<Product> productList = category1.getProducts();
            if (productList.isEmpty()) {
                return ResponseEntity.ok().body("No products found in this category." + productList);
            }
            return ResponseEntity.ok().body(productList);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found.");
    }

     */
    // CREATE REQUEST

    @PostMapping("/add")
    public ResponseEntity<?> add (@Validated @RequestBody Category category){
        Optional<Category> categoryOptional = categoryService.findByCategoryName(category.getCategoryName());
        if(categoryOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("a category with the name " + category.getCategoryName() + " already exists");
        }
        categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category " + category.toString() + " added successfully!");

    }

    // UPDATE REQUEST

    @PutMapping("/modify/{id}")
    public ResponseEntity<?> update(@Validated @RequestBody Category category, @PathVariable Long id){
        Optional<Category> optionalCategory = categoryService.findByCategoryName(category.getCategoryName());
        if(optionalCategory.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("a category with the name " + category.getCategoryName() + " already exists");
        }
        Optional<Category> categoryOptional = categoryService.findById(id);
        if(categoryOptional.isPresent()){
            categoryService.updateCategory(category, id);
            return ResponseEntity.status(HttpStatus.OK).body("Category " + category.toString() + " updated!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category " + category.toString() + " not found");
    }



    //DELETE MAPPING:

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

    /*@DeleteMapping("/deleteProduct/{categoryId}/{productIds}")
    public ResponseEntity<?> deleteProducts(@Validated @PathVariable Long categoryId, @PathVariable List<Long>productIds){
        Optional<Category> categoryOptional = categoryService.findById(categoryId);
        if(categoryOptional.isPresent()){
            categoryService.deleteProductsFromCategory(categoryId, productIds);
            return ResponseEntity.status(HttpStatus.OK).body("Category " + categoryId + " had the products " + productIds + " deleted");

        }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category with the id  " + categoryId + " does not exist on our registers");
        }




    @DeleteMapping("/deleteAllProducts/{categoryId}")
    public ResponseEntity<?> deleteAllProducts(@PathVariable Long categoryId){
        Optional<Category> categoryOptional = categoryService.findById(categoryId);
        if(categoryOptional.isPresent()){
            categoryService.deleteAllProducts(categoryId);
            return ResponseEntity.status(HttpStatus.OK).body("Category " + categoryId + " had all the products deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category with the id  " + categoryId + " does not exist on our registers");
    }

     */

}
