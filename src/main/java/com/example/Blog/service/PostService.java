package com.example.Blog.service;

import com.example.Blog.model.Post;
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
            postTagService.saveOrUpdatePostTag(newPost);
        } else {
            Optional<Post> prePost = postRepository.findById(post.getId());
            if (prePost.isPresent()) {
                String excerpt = post.getContent().substring(0, post.getContent().indexOf("\n"));
                prePost.get().setExcerpt(excerpt);
                prePost.get().setTitle(post.getTitle());
                prePost.get().setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                prePost.get().setContent(post.getContent());
                newPost = postRepository.save(prePost.get());
            }
        }
        return newPost;
    }

    public Optional<Post> getById(Integer id) {
        return postRepository.findById(id);
    }

    public List<Post> getBySearchString(String searchString, Set<Integer> postIds) {
        return postRepository.getBySearchString(searchString, postIds);
    }

    public List<Post> findPostWithSorting(String sortField, String order) {
        if (order.equals("asc")) {
            return postRepository.findAll(Sort.by(Sort.Direction.ASC, sortField));
        } else {
            return postRepository.findAll(Sort.by(Sort.Direction.DESC, sortField));
        }
    }

    public List<Post> findByFiltering(String publishedAt, String filterString, Set<Integer> postIds) {
        return postRepository.findByFiltering(publishedAt, filterString, postIds);
    }

    public void deletePostById(Integer id) {
        postRepository.deleteById(id);
    }

}