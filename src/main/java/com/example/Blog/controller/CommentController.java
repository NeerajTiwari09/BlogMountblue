package com.example.Blog.controller;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.model.Comment;
import com.example.Blog.model.Login;
import com.example.Blog.model.User;
import com.example.Blog.service.impl.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @RequestMapping("/comment")
    public String makeComment(@ModelAttribute("newComment") Comment comment) {
        commentService.saveOrUpdateComment(comment);
        return "redirect:/posts/" + comment.getPostId();
    }

    @GetMapping("/update-comment")
    public String viewUpdateCommentPage(@RequestParam("id") Integer id, Model model) {
        User user = AuthProvider.getAuthenticatedUser();
        if (Objects.isNull(user)) {
            Login login = new Login();
            model.addAttribute("login", login);
            return "login";
        }
        Optional<Comment> comment = commentService.findCommentById(id);
        model.addAttribute("updateComment", comment.get());
        return "update-comment";
    }

    @PostMapping("/update-comment")
    public String updateComment(@ModelAttribute("updateComment") Comment comment) {
        commentService.saveOrUpdateComment(comment);
        return "redirect:/posts/" + comment.getPostId();
    }

    @GetMapping("/delete-comment")
    public String deleteComment(@RequestParam("commentId") Integer commentId, @RequestParam("postId") Integer postId) {
        commentService.deleteCommentById(commentId);
        return "redirect:/posts/" + postId;
    }
}
