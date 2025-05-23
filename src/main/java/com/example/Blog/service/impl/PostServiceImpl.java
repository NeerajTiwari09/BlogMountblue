package com.example.Blog.service.impl;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.dto.input_dto.PostDto;
import com.example.Blog.dto.input_dto.SearchDto;
import com.example.Blog.model.Post;
import com.example.Blog.model.Tag;
import com.example.Blog.model.User;
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
        if (Objects.isNull(post)) {
            User user = AuthProvider.getAuthenticatedUser();
            post = new Post();
            post.setAuthor(user);
            post.setTags(postDto.getTags());
            post.setPublishedAt((new Timestamp(System.currentTimeMillis())).toString());
            post = postRepository.save(post);
        }
        String excerpt = postDto.getContent().substring(0, 50) + "...";
        post.setExcerpt(excerpt);
        post.setPublished(true);
        post.setTags(postDto.getTags());
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post = postRepository.save(post);
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
    public Page<Post> findByFiltering(SearchDto searchDto) {
        int start = searchDto.getOffset();
        int limit = searchDto.getLimit();
        String sortField = searchDto.getSortByField();
        Sort.Direction sortDirection = searchDto.getOrderBy().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(start - 1, limit, Sort.by(sortDirection, sortField));
        User user = userRepository.findById(searchDto.getAuthorId()).orElse(null);
        if (searchDto.getPublishedAt().isEmpty()) {
            return postRepository.findByFilteringWithoutPublishedAt(user, searchDto.getTagIds(), pageable);
        }
        return postRepository.findByFiltering(searchDto.getPublishedAt(), user, searchDto.getTagIds(), pageable);
    }

    @Override
    public void deletePostById(Integer id) {
        postRepository.deleteById(id);
    }

    @Override
    public Page<Post> getAllBlogsByAuthor(User user, SearchDto searchDto) {
        Pageable pageable;
        Sort.Direction sortDirection = searchDto.getOrderBy().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        pageable = PageRequest.of(searchDto.getOffset() - 1, searchDto.getLimit(),
                    Sort.by(sortDirection, searchDto.getSortByField()));

        return postRepository.findAllByAuthor(user, pageable);
    }
}