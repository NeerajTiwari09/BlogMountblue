package com.example.Blog.controller;

import com.example.Blog.response.PostWithComment;
import com.example.Blog.model.Comment;
import com.example.Blog.model.Post;
import com.example.Blog.model.Tag;
import com.example.Blog.model.User;
import com.example.Blog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
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
    public Page<Post> viewHomePage(@RequestParam(name = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "10") int limit, Model model) {
        Page<Post> posts = postService.getAllBlogs(start, limit);
        List<Tag> tags = tagService.findAll();
        List<User> users = userService.findAllAuthors();
        String[] data = new String[]{"asc", "desc"};

        model.addAttribute("users", users);
        model.addAttribute("posts", posts);
        model.addAttribute("start", start);
        model.addAttribute("i", posts.getSize());
        model.addAttribute("tags", tags);
        model.addAttribute("sort",data);
        return posts;
    }

    @GetMapping("/id")
    public PostWithComment getPostById(@RequestParam("id") String id, Model model) {
        Comment newComment = new Comment();
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthorsPost = false;
        String name = userService.findNameByEmail(authentication.getName());
        Optional<Post> post = postService.getById(Integer.valueOf(id));
//        if(post.get().getAuthor().equals(name)){
//            isAuthorsPost=true;
//        }
        List<Comment> comments = commentService.findAllByPostIdOrderCreatedAtDesc(Integer.valueOf(id));
        PostWithComment postWithComment = new PostWithComment();
        postWithComment.setPost(post.get());
        postWithComment.setComments(comments);
        List<Integer> postTags = postTagService.findAllTagIdByPostId(Integer.valueOf(id));
        List<Tag> tags = tagService.findAllById(postTags);

        model.addAttribute("isAuthorsPost", isAuthorsPost);
        model.addAttribute("post", post.get());
        model.addAttribute("tags", tags);
        model.addAttribute("newComment", newComment);
        model.addAttribute("comments", comments);
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
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        System.out.println(post);
        postService.save(email, post);
        List<Tag> tags = tagService.findTagIds(post.getTagString());
        postTagService.saveTagId(tags, post);
        return "redirect:/?start=1&limit=10";
    }

    @PutMapping("/blog/update")
    public String updatePost(@RequestBody Post post, @RequestBody String tag) {
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        post.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        postService.save(email, post);
        List<Tag> tags = tagService.findTagIds(tag);
        postTagService.saveTagId(tags, post);
        return "redirect:/?start=1&limit=10";
    }

    @GetMapping("/blog/update")
    public String showUpdatePostPage(@RequestParam("id") String id, Model model){
        Optional<Post> post = postService.getById(Integer.valueOf(id));
        List<Integer> postTags = postTagService.findAllTagIdByPostId(Integer.valueOf(id));
        List<Tag> tags = tagService.findAllById(postTags);

        model.addAttribute("blogPost", post.get());
        if(tags.size()!=0) {
            model.addAttribute("tag", tags.get(0));
        }
        else{
            model.addAttribute("tag",new Tag());
        }
        return "update-post";
    }

    @DeleteMapping("/blog/delete/{id}")
    public String deletePost(@PathVariable Integer id){
        System.out.println(id);
        postService.deletePostById(id);
        return "Deleted post id- " + id;
    }

    @GetMapping("/search")
    public List<Post> searchByString(@RequestParam("search") String search, Model model) {
        Set<Integer> postIds = postTagService.findPostIdsByTagName(search);
        List<Post> posts = postService.getBySearchString(search, postIds);
        List<Tag> tags = tagService.findAll();
        List<User> users = userService.findAllAuthors();
        String[] data = new String[]{"asc", "desc"};

        model.addAttribute("posts", posts);
        model.addAttribute("users", users);
        model.addAttribute("tags", tags);
        model.addAttribute("sort",data);
        return posts;
    }

    @GetMapping("/sort")
    public List<Post> getPostWithSorting(@RequestParam("sortField") String sortField, @RequestParam("order") String order, Model model) {
        List<Post> posts = postService.findPostWithSorting(sortField, order);
        List<Tag> tags = tagService.findAll();
        List<User> users = userService.findAllAuthors();
        String[] data = new String[]{"asc", "desc"};

        model.addAttribute("posts", posts);
        model.addAttribute("users", users);
        model.addAttribute("tags", tags);
        model.addAttribute("sort",data);
        return posts;
    }

    @GetMapping("/filter")
    public List<Post> filterPosts(@RequestParam(value = "authorId", required = false) int authorId, @RequestParam(name = "tagId", required = false) List<Integer> tagIds, Model model) {
        Set<Integer> postIds = postTagService.findAllPostIdByTagId(tagIds);
        Optional<User> user = userService.findAuthorById(authorId);
        List<Post> posts = postService.findByFiltering(user.get().getName(), postIds);
        List<Tag> tags = tagService.findAll();
        List<User> users = userService.findAllAuthors();
        String[] data = new String[]{"asc", "desc"};

        model.addAttribute("users", users);
        model.addAttribute("posts", posts);
        model.addAttribute("tags", tags);
        model.addAttribute("sort",data);
        return posts;
    }
}