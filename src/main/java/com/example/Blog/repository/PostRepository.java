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

    @Query("select p from Post as p where p.id in :postIds or lower(p.title) like lower(CONCAT('%',:searchString,'%')) or " +
            "lower(p.content) like lower(CONCAT('%',:searchString,'%')) or lower(p.author) like lower(CONCAT('%',:searchString,'%')) or " +
            "lower(p.excerpt) like lower(CONCAT('%',:searchString,'%'))")
    List<Post> getBySearchString(@Param("searchString") String searchString, @Param("postIds") Set<Integer> postIds);

    @Query("select p from Post as p where p.publishedAt like CONCAT(:publishedAt,'%') or p.author = :author or p.id in :ids")
    List<Post> findByFiltering(@Param("publishedAt") String date, @Param("author") String author, @Param("ids") Set<Integer> ids);

    @Query("SELECT p FROM Post as p WHERE lower(p.author) = :author or p.id in :ids")
    List<Post> findByFilteringWithoutPublishedAt(@Param("author") String authorName, @Param("ids") Set<Integer> postIds);
}