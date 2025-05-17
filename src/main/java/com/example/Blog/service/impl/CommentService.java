package com.example.Blog.service.impl;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.model.Comment;
import com.example.Blog.model.User;
import com.example.Blog.repository.CommentRepository;
import com.example.Blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Comment> findAllByPostIdOrderCreatedAtDesc(Integer postId) {
        return commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
    }

    public void saveOrUpdateComment(Comment comment) {
        if (comment.getId() != null) {
            Optional<Comment> preComment = commentRepository.findById(comment.getId());
            if (preComment.isPresent()) {
                preComment.get().setComment(comment.getComment());
                preComment.get().setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                commentRepository.save(preComment.get());
            }
        } else {
            User user = AuthProvider.getAuthenticatedUser();
            String name = "anonymousUser";
            if (Objects.nonNull(user)) {
                name = user.getName();
            }
            comment.setName(name);
            comment.setCommenter(user);
            comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            comment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            commentRepository.save(comment);
        }
    }

    public Optional<Comment> findCommentById(Integer id) {
        return commentRepository.findById(id);
    }

    public void deleteCommentById(Integer commentId) {
        commentRepository.deleteById(commentId);
    }
}