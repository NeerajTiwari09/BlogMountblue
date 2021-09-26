package com.example.Blog.controller;

import com.example.Blog.model.Post;
import com.example.Blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Controller
public class AppController {

    @Autowired
    private PostService postService;

    @RequestMapping("/")
    public String viewHomePage(@RequestParam("start") int start, @RequestParam("limit") int limit, Model model) {
//        List<Post> posts = postService.listAll();
        Page<Post> posts = postService.getAllBlogs(start, limit);
        model.addAttribute("posts", posts);
        model.addAttribute("start", start);
        return "first";
    }

    @RequestMapping("/id")
    public String getPostById(@RequestParam("id") String id, Model model){
        Optional<Post> post = postService.getById(Integer.valueOf(id));
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

    @RequestMapping("/sort")
    public String sort(/*@RequestParam("sortField") String sortField, @RequestParam("order") String order*/ Model model){
        List<Post> posts = postService.findByOrderByPublishedAtAcs();
        model.addAttribute("posts", posts);

        return "first";
    }
}
