package com.example.Blog.controller;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.dto.input_dto.PostDto;
import com.example.Blog.dto.input_dto.SearchDto;
import com.example.Blog.model.Comment;
import com.example.Blog.model.Post;
import com.example.Blog.model.Tag;
import com.example.Blog.model.User;
import com.example.Blog.service.PostService;
import com.example.Blog.service.impl.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.Blog.constant.Constants.ORDER_BY;
import static com.example.Blog.constant.Constants.SORT_BY;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private TagService tagService;
    @Autowired
    private UserService userService;

    @GetMapping(value = {"/", ""})
    public String viewHomePage(@RequestParam(name = "start", defaultValue = "1") int start, @RequestParam(value = "limit", defaultValue = "10") int limit, Model model) {
        Page<Post> posts = postService.getAllBlogs(start, limit);
        List<Tag> tags = tagService.findAll();
        List<User> users = userService.findAllAuthors();

        model.addAttribute("users", users);
        model.addAttribute("posts", posts);
        model.addAttribute("start", start);
        model.addAttribute("currentPage", posts.getPageable().getPageNumber());
        model.addAttribute("tags", tags);
        model.addAttribute("sort", ORDER_BY);
        model.addAttribute("sortBy", SORT_BY);
        return "home";
    }

    @RequestMapping("/{id}")
    public String getPostById(@PathVariable("id") int id, Model model) {
        Comment newComment = new Comment();
        User user = AuthProvider.getAuthenticatedUser();
        boolean isAuthorsPost = false;
        Post post = postService.getById(id).orElse(null);
        if (Objects.nonNull(user) && Objects.nonNull(post) && post.getAuthor().getUsername().equals(user.getUsername())) {
            isAuthorsPost = true;
        }
        List<Comment> comments = commentService.findAllByPostIdOrderCreatedAtDesc(Integer.valueOf(id));
        model.addAttribute("isAuthorsPost", isAuthorsPost);
        model.addAttribute("post", post);
        model.addAttribute("newComment", newComment);
        model.addAttribute("comments", comments);
        return "post";
    }

    @RequestMapping("/new")
    public String createNewPost(Model model) {
        PostDto post = new PostDto();
        List<Tag> tags = tagService.findAll();
        model.addAttribute("blogPost", post);
        model.addAttribute("allTags", tags);
        return "new-post";
    }

    @RequestMapping("/publish")
    public String publishPost(@ModelAttribute("blogPost") PostDto postDto, RedirectAttributes redirectAttributes) {
        List<Tag> tags = tagService.findTagIds(postDto);
        postDto.setTags(new HashSet<>(tags));
        Post newPost = postService.publishPost(postDto);
        redirectAttributes.addFlashAttribute("toastMessage", "Blog created successfully!");
        redirectAttributes.addFlashAttribute("toastStatusColor", "bg-success");
        return "redirect:/posts/" + newPost.getId();
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute("blogPost") PostDto postDto, RedirectAttributes redirectAttributes) {
        List<Tag> tags = tagService.findTagIds(postDto);
        postDto.setTags(new HashSet<>(tags));
        Post newPost = postService.publishPost(postDto);
        redirectAttributes.addFlashAttribute("toastMessage", "Blog updated successfully!");
        redirectAttributes.addFlashAttribute("toastStatusColor", "bg-success");
        return "redirect:/posts/" + newPost.getId();
    }

    @GetMapping("/update")
    public String showUpdatePostPage(@RequestParam("id") String id, Model model) {
        Optional<Post> post = postService.getById(Integer.valueOf(id));
        PostDto postDto = new PostDto();
        BeanUtils.copyProperties(post.get(), postDto);
        postDto.setSelectedTags(post.get().getTags().stream().map(Tag::getId).collect(Collectors.toList()));
        List<Tag> allTags = tagService.findAll();
        model.addAttribute("blogPost", postDto);
        model.addAttribute("allTags", allTags);
        return "update-post";
    }

    @GetMapping("/delete")
    public String deletePost(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        postService.deletePostById(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Blog deleted successfully!");
        redirectAttributes.addFlashAttribute("toastStatusColor", "bg-success");
        return "redirect:/posts";
    }

    @RequestMapping("/search")
    public String searchByString(@RequestParam("search") String search,
                                 @RequestParam(value = "start", required = false, defaultValue = "1") int start,
                                 @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                 @RequestParam(value = "sortField", defaultValue = "publishedAt") String sortField,
                                 @RequestParam(value = "order", defaultValue = "aes") String order,
                                 Model model) {
        SearchDto searchDto = new SearchDto();
        searchDto.setLimit(limit);
        searchDto.setOffset(start);
        searchDto.setOrderBy(order);
        searchDto.setSortByField(sortField);
        searchDto.setSearchSting(search);
        Page<Post> posts = postService.getBySearchString(searchDto);
        List<Tag> tags = tagService.findAll();
        List<User> users = userService.findAllAuthors();

        model.addAttribute("posts", posts);
        model.addAttribute("users", users);
        model.addAttribute("tags", tags);
        model.addAttribute("currentPage", posts.getPageable().getPageNumber());
        model.addAttribute("sort", ORDER_BY);
        model.addAttribute("sortBy", SORT_BY);
        model.addAttribute("selectedOrder", order);
        model.addAttribute("selectedSortBy", sortField);
        model.addAttribute("searchedText", search);
        return "home";
    }

    @RequestMapping("/filter")
    public String filterPosts(@RequestParam(name = "publishedAt", required = false, defaultValue = "") String publishedAt,
                              @RequestParam(name = "authorId", required = false, defaultValue = "0") int authorId,
                              @RequestParam(name = "tagId", required = false) List<Integer> tagIds,
                              @RequestParam(value = "start", required = false, defaultValue = "1") int start,
                              @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                              @RequestParam(value = "sortField", required = false, defaultValue = "publishedAt") String sortField,
                              @RequestParam(value = "order", required = false, defaultValue = "aes") String order,
                              Model model) {
        SearchDto searchDto = new SearchDto();
        searchDto.setLimit(limit);
        searchDto.setOffset(start);
        searchDto.setOrderBy(order);
        searchDto.setSortByField(sortField);
        searchDto.setAuthorId(authorId);
        searchDto.setPublishedAt(publishedAt);
        searchDto.setTagIds(tagIds);
        Page<Post> posts = postService.findByFiltering(searchDto);
        List<Tag> tags = tagService.findAll();
        List<User> users = userService.findAllAuthors();

        model.addAttribute("users", users);
        model.addAttribute("posts", posts);
        model.addAttribute("tags", tags);
        model.addAttribute("start", start);
        model.addAttribute("currentPage", posts.getPageable().getPageNumber());
        model.addAttribute("selectedOrder", order);
        model.addAttribute("sort", ORDER_BY);
        model.addAttribute("sortBy", SORT_BY);
        model.addAttribute("selectedAuthorId", authorId);
        model.addAttribute("selectedTagIds", tagIds);
        model.addAttribute("selectedPublishedAt", publishedAt);
        model.addAttribute("selectedSortBy", sortField);
        return "home";
    }

    @GetMapping("/my-blog")
    public String fetchLoggedInUserBlog(@RequestParam(value = "start", required = false, defaultValue = "1") int start,
                                        @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                        @RequestParam(value = "sortField", defaultValue = "publishedAt") String sortField,
                                        @RequestParam(value = "order", defaultValue = "aes") String order, Model model) {
        SearchDto searchDto = new SearchDto();
        searchDto.setLimit(limit);
        searchDto.setOffset(start);
        searchDto.setOrderBy(order);
        searchDto.setSortByField(sortField);
        User user = AuthProvider.getAuthenticatedUser();
        if (Objects.nonNull(user)) {
            Page<Post> posts = postService.getAllBlogsByAuthor(user, searchDto);
            Map<String, Tag> tags = new HashMap<>();
            List<Tag> authorTags = postService.getCurrentAuthorTags();
            authorTags.forEach(tag -> tags.put(tag.getName(), tag));

            model.addAttribute("posts", posts);
            model.addAttribute("users", Collections.singleton(user));
            model.addAttribute("tags", tags.values());
            model.addAttribute("currentPage", posts.getPageable().getPageNumber());
            model.addAttribute("start", start);
            model.addAttribute("selectedOrder", order);
            model.addAttribute("selectedAuthorId", user.getId());
            model.addAttribute("selectedSortBy", sortField);
            model.addAttribute("sort", ORDER_BY);
            model.addAttribute("sortBy", SORT_BY);
            return "user-post";
        }
        return "/error/access-denied";
    }

    @RequestMapping("/my-blog/search")
    public String searchMyBlogByString(@RequestParam("search") String search,
                                 @RequestParam(value = "start", required = false, defaultValue = "1") int start,
                                 @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                 @RequestParam(value = "sortField", defaultValue = "publishedAt") String sortField,
                                 @RequestParam(value = "order", defaultValue = "aes") String order,
                                 Model model) {
        searchByString(search, start, limit, sortField, order, model);
        return "user-post";
    }

    @RequestMapping("/my-blog/filter")
    public String filterMyPosts(@RequestParam(name = "publishedAt", required = false, defaultValue = "") String publishedAt,
                              @RequestParam(name = "authorId", required = false, defaultValue = "0") int authorId,
                              @RequestParam(name = "tagId", required = false) List<Integer> tagIds,
                              @RequestParam(value = "start", required = false, defaultValue = "1") int start,
                              @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                              @RequestParam(value = "sortField", required = false, defaultValue = "publishedAt") String sortField,
                              @RequestParam(value = "order", required = false, defaultValue = "aes") String order,
                              Model model) {
        filterPosts(publishedAt, authorId, tagIds, start, limit, sortField, order, model);
        return "user-post";
    }
}