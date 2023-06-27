package com.example.veterinaria.repository;

import com.example.veterinaria.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    public Optional<Role> findByRoleName(String roleName);

}
