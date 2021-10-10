package com.example.Blog.controller;

import com.example.Blog.model.Comment;
import com.example.Blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/comment")
    public String makeComment(@RequestBody Comment comment) {
        commentService.saveOrUpdateComment(comment);
        return "Comment is: " + comment.getComment();
    }

    @GetMapping("/updateComment/{commentId}")
    public Comment viewUpdateCommentPage(@PathVariable Integer commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Optional<Comment> comment = commentService.findCommentById(commentId);
        return comment.get();
    }

    @PutMapping("/updateComment")
    public String updateComment(@RequestBody Comment comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "Please login first...";
        }
        commentService.saveOrUpdateComment(comment);
        return "New updated comment: " + comment.getComment();
    }

    @DeleteMapping("/deleteComment/{commentId}")
    public String deleteComment(@PathVariable Integer commentId) {
        Optional<Comment> comment = commentService.findCommentById(commentId);
        if (comment.isPresent()) {
            commentService.deleteCommentById(commentId);
            return "Deleted comment id- " + commentId;
        }
        return "Comment not found.";
    }
}