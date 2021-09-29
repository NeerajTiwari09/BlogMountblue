package com.example.Blog.repository;

import com.example.Blog.model.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Integer> {

    @Query("SELECT p.tagId FROM PostTag p where p.postId = :postId")
    List<Integer> findAllByPostId(@Param("postId") Integer postId);
}
