package com.example.Blog.repository;

import com.example.Blog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("select p from Post as p where p.id in :postIds or p.title like ('%',:searchString,'%') or " +
            "p.content like CONCAT('%',:searchString,'%') or p.author like CONCAT('%',:searchString,'%') or " +
            "p.excerpt like CONCAT('%',:searchString,'%')")
    List<Post> getBySearchString(@Param("searchString") String searchString, @Param("postIds") Set<Integer> postIds);

    @Query("select p from Post as p where p.publishedAt like CONCAT(:publishedAt,'%') or p.author = :author or p.id in :ids")
    List<Post> findByFiltering(@Param("publishedAt") String date, @Param("author") String author, @Param("ids") Set<Integer> ids);
}