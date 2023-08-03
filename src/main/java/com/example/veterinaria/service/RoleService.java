package com.example.veterinaria.service;

import com.example.veterinaria.entity.Role;
import com.example.veterinaria.exception.NotFoundException;
import com.example.veterinaria.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor

public class RoleService {

    private final RoleRepository roleRepository;



    public List<Role> list(){
        return roleRepository.findAll();
    }


    public void create(Role role){
        roleRepository.save(role);
    }

    public Optional<Role> findById(Long id){
        return roleRepository.findById(id);
    }


    public void update(Role role, Long id){
        Role role1 = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Role with ID " + id + " not found"));
        if(role1.getRoleName() !=null && !role.getRoleName().isEmpty()) role1.setRoleName(role.getRoleName());
        roleRepository.save(role1);
    }

    public void delete(Long id){
        roleRepository.deleteById(id);
    }



    public Optional<Role> findByRoleName(String roleName){
        return roleRepository.findByRoleName(roleName);
    }
}


