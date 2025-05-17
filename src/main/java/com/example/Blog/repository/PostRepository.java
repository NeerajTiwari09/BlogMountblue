package com.example.Blog.repository;

import com.example.Blog.model.Post;
import com.example.Blog.model.Tag;
import com.example.Blog.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("select p from Post as p JOIN p.tags t where t.id in :tagIds or lower(p.title) like lower(CONCAT('%',:searchString,'%')) or " +
            "lower(p.content) like lower(CONCAT('%',:searchString,'%')) or lower(p.author) like lower(CONCAT('%',:searchString,'%')) or " +
            "lower(p.excerpt) like lower(CONCAT('%',:searchString,'%'))")
    Page<Post> getBySearchString(@Param("searchString") String searchString, @Param("tagIds") Set<Integer> tagIds, Pageable pageable);

    @Query("select p from Post as p JOIN p.tags t where p.publishedAt like CONCAT(:publishedAt,'%') or p.author = :author or t.id in :tagIds")
    Page<Post> findByFiltering(@Param("publishedAt") String date, @Param("author") User author, @Param("tagIds") List<Integer> tagIds, Pageable pageable);

    @Query("SELECT p FROM Post as p JOIN p.tags t WHERE lower(p.author) = :author or t.id in :tagIds")
    Page<Post> findByFilteringWithoutPublishedAt(@Param("author") User author, @Param("tagIds") List<Integer> tagIds, Pageable pageable);

    Page<Post> findAllByAuthor(User author, Pageable pageable);

    @Query("select p.tags from Post as p where p.author = :author")
    List<Tag> findTagsByAuthor(User author);
}