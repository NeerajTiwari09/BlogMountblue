package com.example.Blog.repository;

import com.example.Blog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("select p from Post as p where p.id in :postIds or p.title like CONCAT('%',:searchString,'%') or " +
            "p.content like CONCAT('%',:searchString,'%') or p.author like CONCAT('%',:searchString,'%') or " +
            "p.excerpt like CONCAT('%',:searchString,'%')")
    List<Post> getBySearchString(@Param("searchString") String searchString, @Param("postIds") Set<Integer> postIds);

    //      TODO- add publishedAt and authorId filter
    @Query("select p from Post as p where p.author = ?1 or p.id in ?2")
    List<Post> findByFiltering(@Param("filterString") String filterString, @Param("ids") Set<Integer> ids);
}
