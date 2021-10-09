package com.example.Blog.service;

import com.example.Blog.model.Post;
import com.example.Blog.model.Tag;
import com.example.Blog.repository.PostRepository;
import com.example.Blog.repository.TagRepository;
import com.example.Blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostTagService postTagService;

    public Page<Post> getAllBlogs(int start, int limit) {
        Pageable pageWithTenElements = PageRequest.of(start, limit);
        return postRepository.findAll(pageWithTenElements);
    }

    public Post saveOrUpdatePost(Post post) {
        Post newPost = null;
        if (post.getId() == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            String excerpt = post.getContent().substring(0, post.getContent().indexOf("\n"));
            String author = userRepository.findNameByUsername(email);
            post.setAuthor(author);
            post.setExcerpt(excerpt);
            post.setPublished(true);
            post.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            post.setPublishedAt(new Timestamp(System.currentTimeMillis()));
            post.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            newPost = postRepository.save(post);
//            postTagService.saveTagId(newPost);
            return newPost;
        } else {
            Optional<Post> prePost = postRepository.findById(post.getId());
            if (prePost.isPresent()) {
                String excerpt = post.getContent().substring(0, post.getContent().indexOf("\n"));
                prePost.get().setExcerpt(excerpt);
                prePost.get().setTitle(post.getTitle());
                prePost.get().setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                prePost.get().setContent(post.getContent());
                newPost = postRepository.save(prePost.get());
//                postTagService.saveTagId(newPost);
            }
        }
        return  newPost;
    }

    public Optional<Post> getById(Integer id) {
        Optional<Post> post = postRepository.findById(id);
        StringBuilder tags = new StringBuilder();
        if (post.isPresent()) {
            for (Tag tag : post.get().getTags()) {
                tags.append(tag.getName()).append(" ");
            }
            post.get().setTagString(tags.toString());
        }
        return post;
    }

    public List<Post> getBySearchString(String searchString, Set<Integer> postIds) {
        return postRepository.getBySearchString(searchString, postIds);
    }

    public Page<Post> findPostWithSorting(String sortField, String order, int offSet, int pageSize) {

        if (order.equals("asc")) {
            Pageable pageable = PageRequest.of(offSet, pageSize, Sort.by(Sort.Direction.ASC, sortField));
            return postRepository.findAll(pageable);
        } else {
            Pageable pageable = PageRequest.of(offSet, pageSize, Sort.by(Sort.Direction.DESC, sortField));
            return postRepository.findAll(pageable);
        }
    }

    public Page<Post> findByFiltering(String publishedAt, String authorName, Set<Integer> postIds, int offSet, int pageSize) {
        Pageable pageable = PageRequest.of(offSet, pageSize);
        return postRepository.findByFiltering(publishedAt, authorName, postIds, pageable);
    }

    public void deletePostById(Integer id) {
        postRepository.deleteById(id);
    }
}