package com.example.Blog.repository;

import com.example.Blog.enums.RoleName;
import com.example.Blog.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(RoleName name);
}
