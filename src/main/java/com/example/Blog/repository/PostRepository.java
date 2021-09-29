package com.example.Blog.repository;

import com.example.Blog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

        @Query("select p from Post as p where p.title like CONCAT('%',:searchString,'%') or " +
                "p.content like CONCAT(:searchString) or p.author like CONCAT(:searchString) or p.excerpt like CONCAT(:searchString)")
        List<Post> getBySearchString(@Param("searchString") String searchString);
}
