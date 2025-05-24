package com.example.Blog.repository;

import com.example.Blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
    User findByUsername(@Param("username") String email);

    @Query("SELECT u.name FROM User u WHERE u.username = :email")
    String findNameByUsername(@Param("email") String email);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.id != :id")
    List<User> findAllExcept(Integer id);
}
