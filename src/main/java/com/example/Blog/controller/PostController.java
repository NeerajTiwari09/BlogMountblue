package com.example.Blog.controller;

import com.example.Blog.response.PostWithComment;
import com.example.Blog.model.Comment;
import com.example.Blog.model.Post;
import com.example.Blog.model.Tag;
import com.example.Blog.model.User;
import com.example.Blog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
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
    public Page<Post> viewHomePage(@RequestParam(name = "start", defaultValue = "0") int start,
                                   @RequestParam(value = "limit", defaultValue = "10") int limit,
                                   Model model) {
        return postService.getAllBlogs(start, limit);
    }

    @GetMapping("/id")
    public PostWithComment getPostById(@RequestParam("id") String id) {
        PostWithComment postWithComment = new PostWithComment();
        Optional<Post> post = postService.getById(Integer.valueOf(id));
        List<Comment> comments = commentService.findAllByPostIdOrderCreatedAtDesc(Integer.valueOf(id));
        post.ifPresent(postWithComment::setPost);
        postWithComment.setComments(comments);
        return postWithComment;
    }

    @GetMapping("/blog/new")
    public String createNewPost(Model model) {
        Post post = new Post();
        Tag tag = new Tag();
        model.addAttribute("blogPost", post);
        model.addAttribute("tag", tag);
        return "new-post";
    }

    @PostMapping("/blog/publish")
    public String publishPost(@RequestBody Post post) {
        List<Tag> tags = tagService.findTagIds(post.getTagString());
        post.setTags(new HashSet<>(tags));
        postService.saveOrUpdatePost(post);
        return "Post published";
    }

    @PutMapping("/blog/update")
    public String updatePost(@RequestBody Post post) {
        List<Tag> tags = tagService.findTagIds(post.getTagString());
        post.setTags(new HashSet<>(tags));
        postService.saveOrUpdatePost(post);
        return "Post updated";
    }

    @GetMapping("/blog/update/{id}")
    public Post showUpdatePostPage(@PathVariable String id) {
        Optional<Post> post = postService.getById(Integer.valueOf(id));
        return post.orElse(null);
    }

    @DeleteMapping("/blog/delete/{id}")
    public String deletePost(@PathVariable Integer id) {
        postService.deletePostById(id);
        return "Deleted post id- " + id;
    }

    @GetMapping("/search")
    public List<Post> searchByString(@RequestParam("search") String search) {
        Set<Integer> postIds = postTagService.findPostIdsByTagName(search);
        return postService.getBySearchString(search, postIds);
    }

    @GetMapping("/sort")
    public List<Post> getPostWithSorting(@RequestParam("sortField") String sortField,
                                         @RequestParam("order") String order) {
        return postService.findPostWithSorting(sortField, order);
    }

    @GetMapping("/filter")
    public List<Post> filterPosts(@RequestParam(value = "authorId", required = false) int authorId,
                                  @RequestParam(name = "tagId", required = false) List<Integer> tagIds) {
        Set<Integer> postIds = postTagService.findAllPostIdByTagId(tagIds);
        Optional<User> user = userService.findAuthorById(authorId);
        if (user.isPresent()) {
            return postService.findByFiltering(user.get().getName(), postIds);
        } else {
            return postService.findByFiltering("", postIds);
        }
    }
}