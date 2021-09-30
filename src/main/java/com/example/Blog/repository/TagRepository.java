package com.example.Blog.repository;

import com.example.Blog.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {


    boolean existsByName(String name);
//
    List<Tag> findAllByName(String name);
}
