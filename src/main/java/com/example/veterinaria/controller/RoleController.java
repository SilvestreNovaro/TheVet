package com.example.veterinaria.controller;

import com.example.veterinaria.entity.Role;
import com.example.veterinaria.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RestControllerAdvice
@Validated
@RequestMapping("/role")
public class RoleController {


    private final RoleService roleService;


    @GetMapping("/list")
    public List<Role> list() {
        return roleService.list();
    }

    @PostMapping("/create")
    public ResponseEntity<String> add(@Validated @RequestBody Role role) {
        roleService.create(role);
        return ResponseEntity.status(HttpStatus.CREATED).body("Role " + role.getRoleName() + " created successfully");
    }


    @PatchMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody Role role, @PathVariable Long id){
       roleService.update(role, id);
       return ResponseEntity.status(HttpStatus.OK).body("Role with ID " + id + " updated successfully");
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
            roleService.delete(id);
        return ResponseEntity.ok("Role deleted");
    }


    @GetMapping("/find/{id}")
    public Optional<Role> findById(@PathVariable Long id) {
        return roleService.findById(id);
    }


    @GetMapping("/findByName/{roleName}")
    public ResponseEntity<Object> findByName(@PathVariable String roleName){
       Optional<Role> roleOptional = roleService.findByRoleName2(roleName);
       return ResponseEntity.ok(roleOptional);
    }



}
