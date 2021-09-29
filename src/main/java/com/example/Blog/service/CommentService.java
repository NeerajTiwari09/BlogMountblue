package com.example.Blog.service;

import com.example.Blog.model.Comment;
import com.example.Blog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;


//    public List<Comment> findByPostId() {
//        return commentRepository.findAllByPostId();
//    }

    public List<Comment> findAllByPostIdOrderCreatedAtDesc(Integer postId) {
        return commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
    }

    public void saveComment(Comment comment) {
        comment.setEmail("mahindra@gmail.com");
        comment.setName("Mahindra");
        comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        comment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        commentRepository.save(comment);
    }
}
