package com.example.Blog.controller;

import com.example.Blog.model.Comment;
import com.example.Blog.model.Post;
import com.example.Blog.model.Tag;
import com.example.Blog.model.User;
import com.example.Blog.service.CommentService;
import com.example.Blog.service.PostService;
import com.example.Blog.service.PostTagService;
import com.example.Blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class AppController {

    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private TagService tagService;
    @Autowired
    private PostTagService postTagService;

    @GetMapping("/")
    public String viewHomePage(@RequestParam(name = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "10") int limit, Model model) {
        Page<Post> posts = postService.getAllBlogs(start, limit);
        List<Tag> tags = tagService.findAll();

        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setName("Neeraj");
        User user2 = new User();
        user2.setName("Manish");
        User user3 = new User();
        user3.setName("Amit");
        users.add(user1);users.add(user2);users.add(user3);
        model.addAttribute("users", users);

        model.addAttribute("posts", posts);
        model.addAttribute("start", start);
        model.addAttribute("i", posts.getSize());
        model.addAttribute("tags", tags);
        String[] data = new String[]{"asc", "desc"};
        model.addAttribute("sort",data);
        return "first";
    }

    @RequestMapping("/id")
    public String getPostById(@RequestParam("id") String id, Model model) {
        Comment newComment = new Comment();
        Optional<Post> post = postService.getById(Integer.valueOf(id));
        List<Comment> comments = commentService.findAllByPostIdOrderCreatedAtDesc(Integer.valueOf(id));
        List<Integer> postTags = postTagService.findAllTagIdByPostId(Integer.valueOf(id));
        List<Tag> tags = tagService.findAllById(postTags);
        model.addAttribute("post", post.get());
        model.addAttribute("tags", tags);
        model.addAttribute("newComment", newComment);
        model.addAttribute("comments", comments);
        return "post";
    }


    @RequestMapping("/new")
    public String createPost(Model model) {
        Post post = new Post();
        Tag tag = new Tag();
        model.addAttribute("blogPost", post);
        model.addAttribute("tag", tag);
        return "new-post";
    }

    @RequestMapping("/publish")
    public String publishPost(@ModelAttribute("blogPost") Post post, @ModelAttribute("tag") Tag tag) {
        postService.save(post, tag);
        List<Tag> tags = tagService.findTagIds(tag);
        postTagService.saveTagId(tags, post);
        return "redirect:/?start=1&limit=10";
    }

    @RequestMapping("/search")
    public String searchByString(@RequestParam("search") String search, Model model) {
        Set<Integer> postIds = postTagService.findPostIdsByTagName(search);
        List<Post> posts = postService.getBySearchString(search, postIds);
        model.addAttribute("posts", posts);
//        model.addAttribute("start", start);
//        model.addAttribute("i",posts.getSize());
        return "";
    }

    @RequestMapping("/sort")
    public String getPostWithSorting(@RequestParam("sortField") String sortField, @RequestParam("order") String order, Model model) {
        List<Post> posts = postService.findPostWithSorting(sortField, order);
        model.addAttribute("posts", posts);
        return "first";
    }

    @RequestMapping("/filter")
    public String filterPosts(@RequestParam("authorId") int authorId, @RequestParam("tagId") List<Integer> tagIds) {
        Set<Integer> postIds = postTagService.findAllPostIdByTagId(tagIds);
        List<Post> posts = postService.findByFiltering("", postIds);
        return "";
    }
}
