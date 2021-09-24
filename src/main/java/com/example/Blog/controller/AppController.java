package com.example.Blog.controller;

import com.example.Blog.model.Post;
import com.example.Blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class AppController {

    @Autowired
    private PostService postService;

    @RequestMapping("/")
    public String viewHomePage(Model model){
        List<Post> posts = postService.listAll();
        model.addAttribute("posts", posts);
        return "first";
    }

    @RequestMapping("/new")
    public String createPost(Model model){
        Post post = new Post();
        model.addAttribute("post", post);
        return "new-post";
    }

}
