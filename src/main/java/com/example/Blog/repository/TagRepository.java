package com.example.Blog.repository;

import com.example.Blog.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    Tag findByName(String name);

    @Query("SELECT t FROM Tag t where t.name in :tagsName")
    List<Tag> findAllByName(@Param("tagsName") List<String> name);

    @Query("SELECT t.id FROM Tag t where t.name like CONCAT(:tagsName,'%')")
    Set<Integer> findIdByName(String tagsName);
}
