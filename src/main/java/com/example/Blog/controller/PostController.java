package com.example.Blog.controller;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.constant.ToastConstant;
import com.example.Blog.dto.UserDto;
import com.example.Blog.dto.input_dto.LikeDto;
import com.example.Blog.dto.input_dto.PostDto;
import com.example.Blog.dto.input_dto.SearchDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.model.Comment;
import com.example.Blog.model.Post;
import com.example.Blog.model.Tag;
import com.example.Blog.model.User;
import com.example.Blog.service.LikeService;
import com.example.Blog.service.PostService;
import com.example.Blog.service.SavePostService;
import com.example.Blog.service.impl.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private LikeService likeService;
    @Autowired
    private SavePostService savePostService;

    @RequestMapping("/{id}")
    public String getPostById(@PathVariable("id") int id, Model model) {
        Comment newComment = new Comment();
        User user = AuthProvider.getAuthenticatedUser();
        boolean isAuthorsPost = false;
        Post post = postService.getById(id).orElse(null);
        Integer likesCount = likeService.getLikeCountByPost(post);
        boolean likedByCurrentUser = likeService.likedByCurrentUser(post, user);
        if (Objects.nonNull(user) && Objects.nonNull(post) && post.getAuthor().getUsername().equals(user.getUsername())) {
            isAuthorsPost = true;
        }
        List<Comment> comments = commentService.findAllByPostIdOrderCreatedAtDesc(id);
        model.addAttribute("isAuthorsPost", isAuthorsPost);
        model.addAttribute("post", post);
        model.addAttribute("newComment", newComment);
        model.addAttribute("comments", comments);
        model.addAttribute("likeForm", new LikeDto());
        model.addAttribute("likesCount", likesCount);
        model.addAttribute("isPostLikedByCurrentUser", likedByCurrentUser);
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
    public String showUpdatePostPage(@RequestParam("id") String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Post> post = postService.getById(Integer.valueOf(id));
        PostDto postDto = new PostDto();
        if(post.isPresent()){
            BeanUtils.copyProperties(post.get(), postDto);
            postDto.setSelectedTags(post.get().getTags().stream().map(Tag::getId).collect(Collectors.toList()));
            List<Tag> allTags = tagService.findAll();
            model.addAttribute("blogPost", postDto);
            model.addAttribute("allTags", allTags);
            return "update-post";
        }
        else {
            redirectAttributes.addFlashAttribute("toastMessage", "Blog doesn't exist!");
            redirectAttributes.addFlashAttribute("toastStatusColor", "bg-danger");
            return "redirect:/posts";
        }
    }

    @GetMapping("/delete")
    public String deletePost(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        Response<Object> output = postService.deletePostById(id);
        String statusColor = output.isSuccess() ? ToastConstant.TOAST_BG_SUCCESS : ToastConstant.TOAST_BG_DANGER;
        redirectAttributes.addFlashAttribute(ToastConstant.TOAST_MESSAGE, output.getMessage());
        redirectAttributes.addFlashAttribute(ToastConstant.TOAST_STATUS_COLOR, statusColor);
        return "redirect:/posts";
    }

    @RequestMapping("/search")
    public String searchByString(@RequestParam("search") String search,
                                 @RequestParam(value = "start", required = false, defaultValue = "1") int start,
                                 @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                 @RequestParam(value = "sortField", defaultValue = "publishedAt") String sortField,
                                 @RequestParam(value = "order", defaultValue = "desc") String order,
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

    @GetMapping(value = {"/", ""})
    public String filterPosts(@RequestParam(name = "publishedAt", required = false, defaultValue = "") String publishedAt,
                              @RequestParam(name = "authorId", required = false, defaultValue = "0") int authorId,
                              @RequestParam(name = "tagIds", required = false, defaultValue = "") String tagIds,
                              @RequestParam(value = "start", required = false, defaultValue = "1") int start,
                              @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                              @RequestParam(value = "sortField", required = false, defaultValue = "publishedAt") String sortField,
                              @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                              Model model, HttpServletRequest request) {
        List<Integer> intTagIds = new ArrayList<>();
        if(StringUtils.hasText(tagIds)){
            intTagIds = Arrays.stream(tagIds.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        }
        SearchDto searchDto = new SearchDto();
        searchDto.setLimit(limit);
        searchDto.setOffset(start);
        searchDto.setOrderBy(order);
        searchDto.setSortByField(sortField);
        searchDto.setAuthorId(authorId);
        searchDto.setPublishedAt(publishedAt);
        searchDto.setTagIds(intTagIds);
        Page<Post> posts = postService.findByFilterCriteria(searchDto);
        List<Long> postIds = savePostService.getSavedPostIdForUser();
        List<Tag> tags = tagService.findAll();
        List<User> users = userService.findAllAuthors();
        String queryString = request.getQueryString();
        String extraParams = "";
        if (queryString != null) {
            extraParams = Arrays.stream(queryString.split("&"))
                    .filter(p -> !p.startsWith("start=") && !p.startsWith("limit=") && !p.endsWith("="))
                    .collect(Collectors.joining("&"));
            if(!extraParams.isEmpty()){
                extraParams = '&' + extraParams;
            }
        }
        model.addAttribute("users", users);
        model.addAttribute("posts", posts);
        model.addAttribute("savedPostIds", postIds);
        model.addAttribute("tags", tags);
        model.addAttribute("start", start);
        model.addAttribute("currentPage", posts.getPageable().getPageNumber());
        model.addAttribute("selectedOrder", order);
        model.addAttribute("sort", ORDER_BY);
        model.addAttribute("sortBy", SORT_BY);
        model.addAttribute("selectedAuthorId", authorId);
        model.addAttribute("selectedTagIds", intTagIds);
        model.addAttribute("selectedTagIdsForSort", tagIds);
        model.addAttribute("selectedPublishedAt", publishedAt);
        model.addAttribute("selectedSortBy", sortField);
        model.addAttribute("queryParams", extraParams);
        return "home";
    }

    @GetMapping("/my-blog")
    public String fetchLoggedInUserBlog(@RequestParam(name = "publishedAt", required = false, defaultValue = "") String publishedAt,
                                        @RequestParam(name = "authorId", required = false, defaultValue = "0") int authorId,
                                        @RequestParam(name = "tagIds", required = false) String tagIds,
                                        @RequestParam(value = "start", required = false, defaultValue = "1") int start,
                                        @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                        @RequestParam(value = "sortField", required = false, defaultValue = "publishedAt") String sortField,
                                        @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                        Model model, HttpServletRequest request) {
        List<Integer> intTagIds = new ArrayList<>();
        if(StringUtils.hasText(tagIds)){
            intTagIds = Arrays.stream(tagIds.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        }
        SearchDto searchDto = new SearchDto();
        searchDto.setLimit(limit);
        searchDto.setOffset(start);
        searchDto.setOrderBy(order);
        searchDto.setSortByField(sortField);
        searchDto.setPublishedAt(publishedAt);
        searchDto.setAuthorId(authorId);
        searchDto.setTagIds(intTagIds);
        User user = AuthProvider.getAuthenticatedUser();
        String queryString = request.getQueryString();
        String extraParams = "";
        if (queryString != null) {
            extraParams = Arrays.stream(queryString.split("&"))
                    .filter(p -> !p.startsWith("start=") && !p.startsWith("limit="))
                    .collect(Collectors.joining("&"));
            if(!extraParams.isEmpty()){
                extraParams = '&' + extraParams;
            }
        }
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
            model.addAttribute("selectedTagIds", intTagIds);
            model.addAttribute("selectedTagIdsForSort", tagIds);
            model.addAttribute("selectedPublishedAt", publishedAt);
            model.addAttribute("sort", ORDER_BY);
            model.addAttribute("sortBy", SORT_BY);
            model.addAttribute("queryParams", extraParams);
            model.addAttribute("searchUrl", "/posts/my-blog/search");
            model.addAttribute("filterSortUrl", "/posts/my-blog");
            return "home";
        }
        return "/error/access-denied";
    }

    @RequestMapping("/my-blog/search")
    public String searchMyBlogByString(@RequestParam("search") String search,
                                 @RequestParam(value = "start", required = false, defaultValue = "1") int start,
                                 @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                 @RequestParam(value = "sortField", defaultValue = "publishedAt") String sortField,
                                 @RequestParam(value = "order", defaultValue = "desc") String order,
                                 Model model) {
        searchByString(search, start, limit, sortField, order, model);
        model.addAttribute("searchUrl", "/posts/my-blog/search");
        model.addAttribute("filterSortUrl", "/posts/my-blog");
        return "home";
    }

    @PostMapping("/like")
    @ResponseBody
    public Response<LikeDto> likePost(@RequestParam Integer postId){
        return likeService.toggleLike(postId);
    }

    @PostMapping("/liked-by")
    @ResponseBody
    public Response<List<UserDto>> likedBy(@RequestParam Integer postId){
        return likeService.getAllUserByPostId(postId);
    }

    @PostMapping("/api")
    public String loadMorePosts(@RequestBody SearchDto searchDto, Model model) {
        Page<Post> posts = postService.findByFilterCriteria(searchDto);
        List<Long> postIds = savePostService.getSavedPostIdForUser();
        if (posts.getTotalPages() != 0 && posts.isEmpty()) {
            return "fragments/empty :: emptyFragment";
        } else if (posts.isEmpty()) {
            model.addAttribute("notFoundMessage", "No blog post found.");
            return "fragments/empty :: noBlogFoundFragment";
        }
        model.addAttribute("posts", posts.getContent());
        model.addAttribute("savedPostIds", postIds);
        return "fragments/blogposts :: blogpostsFragment";
    }

    @PostMapping("/api/save-post")
    @ResponseBody
    public Response<Object> toggleSavePostForUser(@RequestParam Integer postId) {
        return savePostService.toggleSavePostForUser(postId);
    }

    @GetMapping("/saved-post")
    public String getSavedPostForUser(@RequestParam(name = "publishedAt", required = false, defaultValue = "") String publishedAt,
                                      @RequestParam(name = "authorId", required = false, defaultValue = "0") int authorId,
                                      @RequestParam(name = "tagIds", required = false) String tagIds,
                                      @RequestParam(value = "start", required = false, defaultValue = "1") int start,
                                      @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                      @RequestParam(value = "sortField", required = false, defaultValue = "publishedAt") String sortField,
                                      @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                      Model model, HttpServletRequest request) {
        List<Integer> intTagIds = new ArrayList<>();
        SearchDto searchDto = new SearchDto();
        searchDto.setLimit(limit);
        searchDto.setOffset(start);
        searchDto.setOrderBy(order);
        searchDto.setSortByField("id");
        searchDto.setPublishedAt(publishedAt);
        searchDto.setAuthorId(authorId);
        searchDto.setTagIds(intTagIds);
        User user = AuthProvider.getAuthenticatedUser();
        if (Objects.nonNull(user)) {
            Page<Post> posts = savePostService.getSavedPostForUser(searchDto);
            List<Long> postIds = savePostService.getSavedPostIdForUser();
            Map<String, Tag> tags = new HashMap<>();
            Map<String, User> users = new HashMap<>();
            List<Tag> authorTags = posts.getContent().stream()
                    .flatMap(post -> post.getTags().stream())
                    .collect(Collectors.toList());
            authorTags.forEach(tag -> tags.put(tag.getName(), tag));

            model.addAttribute("posts", posts.getContent());
            model.addAttribute("savedPostIds", postIds);
            model.addAttribute("users", users.values());
            model.addAttribute("tags", tags.values());
            model.addAttribute("currentPage", posts.getPageable().getPageNumber());
            model.addAttribute("start", start);
            model.addAttribute("selectedOrder", order);
            model.addAttribute("selectedAuthorId", user.getId());
            model.addAttribute("selectedSortBy", sortField);
            model.addAttribute("selectedTagIds", intTagIds);
            model.addAttribute("selectedTagIdsForSort", tagIds);
            model.addAttribute("selectedPublishedAt", publishedAt);
            model.addAttribute("sort", ORDER_BY);
            model.addAttribute("sortBy", SORT_BY);
            model.addAttribute("queryParams", "");
            model.addAttribute("searchUrl", "/posts/my-blog/search");
            model.addAttribute("filterSortUrl", "/posts/my-blog");
            return "home";
        }
        return "/error/access-denied";
    }

}