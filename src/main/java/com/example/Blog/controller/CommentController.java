package com.example.Blog.controller;

import com.example.Blog.model.Comment;
import com.example.Blog.model.Login;
import com.example.Blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/comment")
    public String makeComment(@RequestBody Comment comment){
        commentService.saveComment(comment);
        return "redirect:/id?id="+ comment.getPostId();
    }

    @GetMapping("/updateComment")
    public String viewUpdateCommentPage(@RequestParam("id") Integer id, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            Login login = new Login();
            model.addAttribute("login", login);
            return "login";
        }
        Optional<Comment> comment = commentService.findCommentById(id);
        model.addAttribute("updateComment", comment.get());
        return "update-comment";
    }
    @PutMapping("/updateComment")
    public String updateComment(@RequestBody Comment comment){
        comment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        commentService.saveComment(comment);
        return "redirect:/id?id="+ comment.getPostId();
    }

    @DeleteMapping("/deleteComment")
    public String deleteComment(@RequestParam("commentId") Integer commentId, @RequestParam("postId") Integer postId){
        Optional<Comment> comment = commentService.findCommentById(commentId);
        if(comment.get() != null) {
            commentService.deleteCommentById(commentId);
            return "Deleted comment id- " + commentId;
        }
        return "Comment not found.";
    }
}