package com.example.Blog.controller;

import com.example.Blog.model.Post;
import com.example.Blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Controller
public class AppController {

    @Autowired
    private PostService postService;

    @RequestMapping("/")
    public String viewHomePage(Model model) {
        List<Post> posts = postService.listAll();
        model.addAttribute("posts", posts);
        return "first";
    }

    @RequestMapping("/id")
    public String getPostById(@RequestParam("id") Integer id, Model model){
        Optional<Post> post = postService.getById(id);
        model.addAttribute("post", post.get());
        return "post";
    }

    @RequestMapping("/new")
    public String createPost(Model model) {
        Post post = new Post();
        model.addAttribute("blogPost", post);
        return "new-post";
    }

    @RequestMapping("/publish")
    public String publishPost(@ModelAttribute("blogPost") Post post) {
        post.setAuthor("Mahindra");
        post.setPublished(true);
        post.setPublishedAt(new Timestamp(System.currentTimeMillis()));
        post.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        post.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        post.setId(4);
        postService.save(post);
        return "redirect:/";
    }

    @RequestMapping("/search")
    public String search(@RequestParam("search") String search, Model model){
        System.out.println(search);
        List<Post> posts = postService.getBySearchString(search);
        System.out.println(posts);
        model.addAttribute("posts", posts);
        return "first";
    }
}
