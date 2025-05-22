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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = {"", "/"})
    public String makeComment(@ModelAttribute("newComment") Comment comment) {
        commentService.saveOrUpdateComment(comment);
        return "redirect:/posts/" + comment.getPostId();
    }

    @GetMapping("/update")
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

    @PostMapping("/update")
    public String updateComment(@ModelAttribute("updateComment") Comment comment, RedirectAttributes redirectAttributes) {
        commentService.saveOrUpdateComment(comment);
        redirectAttributes.addFlashAttribute("toastMessage", "Comment saved successfully!");
        redirectAttributes.addFlashAttribute("toastStatusColor", "bg-success");
        return "redirect:/posts/" + comment.getPostId();
    }

    @GetMapping("/delete")
    public String deleteComment(@RequestParam("commentId") Integer commentId, @RequestParam("postId") Integer postId,
                                RedirectAttributes redirectAttributes) {
        commentService.deleteCommentById(commentId);
        redirectAttributes.addFlashAttribute("toastMessage", "Comment deleted successfully!");
        redirectAttributes.addFlashAttribute("toastStatusColor", "bg-success");
        return "redirect:/posts/" + postId;
    }
}
