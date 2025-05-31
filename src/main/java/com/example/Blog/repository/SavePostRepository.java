package com.example.Blog.repository;

import com.example.Blog.model.Post;
import com.example.Blog.model.SavedPost;
import com.example.Blog.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SavePostRepository extends JpaRepository<SavedPost, Long> {

    @Query("SELECT s.post FROM SavedPost as s WHERE s.user = :user")
    Page<Post> findAllPostByUser(User user, Pageable pageable);

    @Query("SELECT DISTINCT s.post.id FROM SavedPost as s WHERE s.user.id = :userId")
    List<Long> findAllPostIdByUser(Integer userId);

    Optional<SavedPost> findByPostAndUser(Post post, User currentUser);
}
