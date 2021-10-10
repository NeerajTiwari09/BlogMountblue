package com.example.Blog.controller;

import com.example.Blog.model.Comment;
import com.example.Blog.model.Post;
import com.example.Blog.model.Tag;
import com.example.Blog.model.User;
import com.example.Blog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private TagService tagService;
    @Autowired
    private PostTagService postTagService;
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String viewHomePage(@RequestParam(name = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "10") int limit, Model model) {
        Page<Post> posts = postService.getAllBlogs(start, limit);
        List<Tag> tags = tagService.findAll();
        List<User> users = userService.findAllAuthors();
        String[] order = new String[]{"asc", "desc"};

        model.addAttribute("users", users);
        model.addAttribute("posts", posts);
        model.addAttribute("start", start);
        model.addAttribute("i", posts.getSize());
        model.addAttribute("tags", tags);
        model.addAttribute("sort",order);
        return "first";
    }

    @RequestMapping("/id")
    public String getPostById(@RequestParam("id") String id, Model model) {
        Comment newComment = new Comment();
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName());
        boolean isAuthorsPost = false;
        String name = userService.findNameByEmail(authentication.getName());
        Optional<Post> post = postService.getById(Integer.valueOf(id));
        if(post.get().getAuthor().equals(name)){
            isAuthorsPost=true;
        }
        List<Comment> comments = commentService.findAllByPostIdOrderCreatedAtDesc(Integer.valueOf(id));
        model.addAttribute("isAuthorsPost", isAuthorsPost);
        model.addAttribute("post", post.get());
        model.addAttribute("newComment", newComment);
        model.addAttribute("comments", comments);
        return "post";
    }

    @RequestMapping("/blog/new")
    public String createNewPost(Model model) {
        Post post = new Post();
        model.addAttribute("blogPost", post);
        return "new-post";
    }

    @RequestMapping("/blog/publish")
    public String publishPost(@ModelAttribute("blogPost") Post post) {
        List<Tag> tags = tagService.findTagIds(post.getTagString());
        post.setTags(new HashSet<>(tags));
        Post newPost = postService.saveOrUpdatePost(post);
        return "redirect:/id/?id="+newPost.getId();
    }

    @PostMapping("/blog/update")
    public String updatePost(@ModelAttribute("blogPost") Post post) {
        List<Tag> tags = tagService.findTagIds(post.getTagString());
        post.setTags(new HashSet<>(tags));
        Post newPost = postService.saveOrUpdatePost(post);
        postTagService.updatePostTag(newPost, tags);
        return "redirect:/id/?id="+newPost.getId();
    }

    @GetMapping("/blog/update")
    public String showUpdatePostPage(@RequestParam("id") String id, Model model){
        Optional<Post> post = postService.getById(Integer.valueOf(id));
        model.addAttribute("blogPost", post.get());
        return "update-post";
    }

    @GetMapping("/blog/delete")
    public String deletePost(@RequestParam("id") Integer id){
        postService.deletePostById(id);
        return "redirect:/?start=1&limit=10";
    }

    @RequestMapping("/search")
    public String searchByString(@RequestParam("search") String search, Model model) {
        Set<Integer> postIds = postTagService.findPostIdsByTagName(search);
        List<Post> posts = postService.getBySearchString(search, postIds);
        List<Tag> tags = tagService.findAll();
        List<User> users = userService.findAllAuthors();
        String[] data = new String[]{"asc", "desc"};

        model.addAttribute("posts", posts);
        model.addAttribute("users", users);
        model.addAttribute("tags", tags);
        model.addAttribute("sort",data);
        return "first-page";
    }

    @RequestMapping("/sort")
    public String getPostWithSorting(@RequestParam("sortField") String sortField,
                                     @RequestParam("order") String order,
                                     @RequestParam(value = "start", required = false, defaultValue = "0") int start,
                                     @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                     Model model) {
        Page<Post> posts = postService.findPostWithSorting(sortField, order, start, limit);
        List<Tag> tags = tagService.findAll();
        List<User> users = userService.findAllAuthors();
        String[] data = new String[]{"asc", "desc"};

        model.addAttribute("start", start);
        model.addAttribute("i", posts.getSize());
        model.addAttribute("order", order);
        model.addAttribute("posts", posts);
        model.addAttribute("users", users);
        model.addAttribute("tags", tags);
        model.addAttribute("sort",data);
        return "first-page";
    }

    @RequestMapping("/filter")
    public String filterPosts(@RequestParam(name = "publishedAt", required = false, defaultValue = "") String publishedAt,
                              @RequestParam(name = "authorId", required = false, defaultValue = "0") int authorId,
                              @RequestParam(name = "tagId", required = false) List<Integer> tagIds,
                              @RequestParam(name = "start", required = false, defaultValue = "0") int start,
                              @RequestParam(value = "limit", required = false, defaultValue = "10") int limit, Model model) {
        Set<Integer> postIds = postTagService.findAllPostIdByTagId(tagIds);
        String authorName = userService.findAuthorById(authorId);
        Page<Post> posts = postService.findByFiltering(publishedAt, authorName, postIds, start, limit);
        List<Tag> tags = tagService.findAll();
        List<User> users = userService.findAllAuthors();
        String[] data = new String[]{"asc", "desc"};

        model.addAttribute("start", start);
        model.addAttribute("i", posts.getSize());
        model.addAttribute("users", users);
        model.addAttribute("posts", posts);
        model.addAttribute("tags", tags);
        model.addAttribute("sort",data);
        return "first-page";
    }
}