package com.example.Blog.controller;

import com.example.Blog.model.Comment;
import com.example.Blog.model.Login;
import com.example.Blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @RequestMapping("/comment")
    public String makeComment(@ModelAttribute("newComment") Comment comment){
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
    @PostMapping("/updateComment")
    public String updateComment(@ModelAttribute("updateComment") Comment comment){
        commentService.saveComment(comment);
        return "redirect:/id?id="+ comment.getPostId();
    }

    @GetMapping("/deleteComment")
    public String deleteComment(@RequestParam("commentId") Integer commentId, @RequestParam("postId") Integer postId){
        System.out.println(commentId + " "+ postId);
        commentService.deleteCommentById(commentId);
        return "redirect:/id?id="+ postId;
    }
}
