package com.example.veterinaria.service;

import com.example.veterinaria.entity.Customer;
import com.example.veterinaria.entity.Role;
import com.example.veterinaria.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
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


    public Role add(Role role){
        return roleRepository.save(role);
    }

    public Optional<Role> findById(Long id){
        return roleRepository.findById(id);
    }

    public void update(Role role, Long id){
        Optional<Role> roleOptional = roleRepository.findById(id);
        if(roleOptional.isPresent()){
            Role role1 = roleOptional.get();
            if(role.getRoleName() !=null && !role.getRoleName().isEmpty()) role1.setRoleName(role.getRoleName());
            roleRepository.save(role1);
        }
    }

    public void delete(Long id){
        roleRepository.deleteById(id);
    }



    public Optional<Role> findByRoleName(String roleName){
        return roleRepository.findByRoleName(roleName);
    }
}


