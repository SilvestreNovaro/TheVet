package com.example.veterinaria.service;

import com.example.veterinaria.entity.Role;
import com.example.veterinaria.exception.BadRequestException;
import com.example.veterinaria.exception.ConflictException;
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
        Optional<Role> roleOptional = roleRepository.findByRoleName(role.getRoleName());
        roleOptional.ifPresent(roleFound -> {
            throw new ConflictException("Role with name " + role.getRoleName() + " already exists");
        });
        roleRepository.save(role);
    }


    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id)
                .or(() -> {
                    throw new NotFoundException("Role with the id " + id + " does not exist on our registers");
                });
    }



    public void update(Role role, Long id){
        Role role1 = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Role with ID " + id + " not found"));
        Optional<Role> roleOptional = roleRepository.findByRoleName(role.getRoleName());
        roleOptional.ifPresent(roleFound -> {
            throw new ConflictException("Role with name " + role.getRoleName() + " already exists");
        });
        role1.setRoleName(role.getRoleName());
        roleRepository.save(role1);
    }

    public void delete(Long id) {
        roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with ID " + id + " not found"));
        roleRepository.deleteById(id);
    }


    public Optional<Role> findByRoleName2(String roleName){
        return Optional.ofNullable(roleRepository.findByRoleName(roleName).orElseThrow(() -> new NotFoundException("No role with name " + roleName)));

    }
}


