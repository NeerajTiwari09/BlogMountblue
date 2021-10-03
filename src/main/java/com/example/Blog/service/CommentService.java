package com.example.Blog.service;

import com.example.Blog.model.Comment;
import com.example.Blog.repository.CommentRepository;
import com.example.Blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;

@Service
public class CommentService {


    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;


    public List<Comment> findAllByPostIdOrderCreatedAtDesc(Integer postId) {
        return commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
    }

    public void saveComment(Comment comment) {
        String name = userRepository.findNameByUsername(comment.getEmail());
        comment.setName(name);
        comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        comment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        commentRepository.save(comment);
    }
}
