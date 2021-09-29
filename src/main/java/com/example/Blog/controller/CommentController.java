package com.example.Blog.controller;

import com.example.Blog.model.Comment;
import com.example.Blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @RequestMapping("/comment")
    public String makeComment(@ModelAttribute("newComment") Comment comment){
        System.out.println("Comment Request");
        commentService.saveComment(comment);
        return "redirect:/id?id="+ comment.getPostId();
    }
}
