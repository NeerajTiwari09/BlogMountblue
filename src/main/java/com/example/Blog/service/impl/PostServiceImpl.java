package com.example.Blog.service.impl;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.dto.input_dto.PostDto;
import com.example.Blog.dto.input_dto.SearchDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.enums.ErrorCode;
import com.example.Blog.enums.NotificationMessage;
import com.example.Blog.enums.SuccessCode;
import com.example.Blog.model.Like;
import com.example.Blog.model.Post;
import com.example.Blog.model.Tag;
import com.example.Blog.model.User;
import com.example.Blog.repository.LikeRepository;
import com.example.Blog.repository.PostRepository;
import com.example.Blog.repository.TagRepository;
import com.example.Blog.repository.UserRepository;
import com.example.Blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.*;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Page<Post> getAllBlogs(int start, int limit) {
        Pageable pageWithTenElements = PageRequest.of(start - 1, limit);
        return postRepository.findAll(pageWithTenElements);
    }

    @Override
    public Post publishPost(PostDto postDto){
        return saveOrUpdatePost(postDto);
    }

    private Post saveOrUpdatePost(PostDto postDto) {
        Post post = postRepository.findById(postDto.getId()).orElse(null);
        User user = AuthProvider.getAuthenticatedUser();
        if (Objects.isNull(post)) {
            post = new Post();
            post.setAuthor(user);
            post.setTags(postDto.getTags());
            post.setPublishedAt((new Timestamp(System.currentTimeMillis())).toString());
            post = postRepository.save(post);
        }
        String excerpt = postDto.getContent();
        if(postDto.getContent().length() > 200){
            excerpt = postDto.getContent().substring(0, 200) + "...";
        }
        post.setExcerpt(excerpt);
        post.setPublished(true);
        post.setTags(postDto.getTags());
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post = postRepository.save(post);
        userRepository.findAllExcept(user.getId()).forEach(u ->
                notificationService.sendNotification(u,
                        NotificationMessage.NEW_BLOG_POST.formatMessage(user.getName()))
        );
        return post;
    }

    @Override
    public Optional<Post> getById(Integer id) {
        return postRepository.findById(id);
    }

    public Page<Post> getBySearchString(SearchDto searchDto) {
        int start = searchDto.getOffset();
        int limit = searchDto.getLimit();
        String sortField = searchDto.getSortByField();
        Sort.Direction sortDirection = searchDto.getOrderBy().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(start - 1, limit, Sort.by(sortDirection, sortField));
        Set<Integer> tagIds = tagRepository.findIdByName(searchDto.getSearchSting());
        return postRepository.getBySearchString(searchDto.getSearchSting(), tagIds, pageable);
    }

    @Override
    public List<Tag> getCurrentAuthorTags() {
        User loggedInAuthor = AuthProvider.getAuthenticatedUser();
        if(Objects.nonNull(loggedInAuthor)) {
            return postRepository.findTagsByAuthor(loggedInAuthor);
        }
        return Collections.emptyList();
    }

    @Override
    public Page<Post> findByFilterCriteria(SearchDto searchDto) {
        User user = userRepository.findById(searchDto.getAuthorId()).orElse(null);
        return getPostBySearchCriteria(user, searchDto);
    }

    @Override
    public Response<Object> deletePostById(Integer id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            likeRepository.deleteAllByPost(post.get());
            postRepository.deleteById(id);
            return new Response<>(SuccessCode.BLOG_DELETED_SUCCESSFUL);
        }
        return new Response<>(ErrorCode.INVALID_POST);
    }

    @Override
    public Page<Post> getAllBlogsByAuthor(User user, SearchDto searchDto) {
        return getPostBySearchCriteria(user, searchDto);
    }

    private Page<Post> getPostBySearchCriteria(User user, SearchDto searchDto){
        int start = searchDto.getOffset();
        int limit = searchDto.getLimit();
        String sortField = searchDto.getSortByField();
        Sort.Direction sortDirection = searchDto.getOrderBy().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(start - 1, limit, Sort.by(sortDirection, sortField));
        if (StringUtils.hasText(searchDto.getPublishedAt()) && Objects.nonNull(user) &&
                !ObjectUtils.isEmpty(searchDto.getTagIds())) {
            return postRepository.findByPublishedAtAndUserAndTagIds(searchDto.getPublishedAt(), user, searchDto.getTagIds(), pageable);
        } else if (Objects.nonNull(user) && !ObjectUtils.isEmpty(searchDto.getTagIds())) {
            return postRepository.findByUserAndTagIds(user, searchDto.getTagIds(), pageable);
        } else if (Objects.isNull(user) && !ObjectUtils.isEmpty(searchDto.getTagIds())) {
            return postRepository.findByTagIds(searchDto.getTagIds(), pageable);
        } else if (Objects.nonNull(user) && StringUtils.hasText(searchDto.getPublishedAt())) {
            return postRepository.findAllByAuthorAndPublishedAt(user, searchDto.getPublishedAt(), pageable);
        } else if (Objects.nonNull(user)) {
            return postRepository.findAllByAuthor(user, pageable);
        } else if (StringUtils.hasText(searchDto.getPublishedAt())){
            return postRepository.findAllByPublishedAt(searchDto.getPublishedAt(), pageable);
        }
        return postRepository.findAll(pageable);
    }
}