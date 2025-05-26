package com.example.Blog.repository;

import com.example.Blog.model.Like;
import com.example.Blog.model.Post;
import com.example.Blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    Integer countByPost(Post post);

    Optional<Like> findByPostAndUser(Post post, User user);

    @Transactional
    void deleteAllByPost(Post post);

    List<Like> findAllUserByPost(Post post);
}
