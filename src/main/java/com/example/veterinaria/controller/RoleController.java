package com.example.veterinaria.controller;

import com.example.veterinaria.entity.Role;
import com.example.veterinaria.service.CustomerService;
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

    private final CustomerService customerService;

    @GetMapping("/list")
    public List<Role> list() {
        return roleService.list();
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Validated @RequestBody Role role) {
        Optional<Role> optionalRole = roleService.findByRoleName(role.getRoleName());
        if (optionalRole.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role with name + " + role.getRoleName() + "already exists");
        }
        roleService.add(role);
        return ResponseEntity.status(HttpStatus.CREATED).body("Role created succesfully");
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<?> update(@Validated @RequestBody Role role, @PathVariable Long id) {
        Optional<Role> roleOptional = roleService.findByRoleName(role.getRoleName());
        Optional<Role> optionalRole = roleService.findById(id);
        if (roleOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role " + role.getRoleName() + " already exists");
        }
        if (optionalRole.isPresent()) {
            roleService.update(role, id);
            return ResponseEntity.status(HttpStatus.OK).body("Role " + role.getRoleName() + " updated succesfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role with the id  " + id + " does not exist on our registers");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delte(@Validated @PathVariable Long id) {
        Optional<Role> roleOptional = roleService.findById(id);
        if (roleOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role with the id  " + id + " does not exist on our registers");
        }
        if (!customerService.findCustomerByRoleId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role with the id  " + id + " cant be deleted becouse is asigned to an entity");

        }
            roleService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("Role with the id " + id + " deleted");
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> findById(@Validated @PathVariable Long id){
        Optional<Role> roleOptional = roleService.findById(id);
        if(roleOptional.isPresent()){
            return ResponseEntity.ok(roleOptional);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role with the id  " + id + " does not exist on our registers");
        }
    }





}
