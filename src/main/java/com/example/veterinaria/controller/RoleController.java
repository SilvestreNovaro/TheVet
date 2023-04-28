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
    public List<Role> list(){
        return roleService.list();
    }

    @PostMapping("/add")
    public ResponseEntity<?> add (@Validated @RequestBody Role role){
        Optional<Role> optionalRole = roleService.findByRoleName(role.getRoleName());
        if(optionalRole.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role with name + " + role.getRoleName() + "already exists");
        }
        roleService.add(role);
        return ResponseEntity.status(HttpStatus.CREATED).body("Role created succesfully");
    }


}
