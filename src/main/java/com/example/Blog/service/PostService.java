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

    public Page<Post> getAllBlogs(int start, int limit) {
        Pageable pageWithTenElements = PageRequest.of(start, limit);
        return postRepository.findAll(pageWithTenElements);
    }

    public List<Post> listAll() {
        return postRepository.findAll();
    }

    public void saveOrUpdatePost(Post post) {
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
            postRepository.save(post);
        } else {
            Optional<Post> prePost = postRepository.findById(post.getId());
            if (prePost.isPresent()) {
                String excerpt = post.getContent().substring(0, post.getContent().indexOf("\n"));
                prePost.get().setExcerpt(excerpt);
                prePost.get().setTitle(post.getTitle());
                prePost.get().setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                prePost.get().setContent(post.getContent());
                postRepository.save(prePost.get());
            }
        }
    }

    public Optional<Post> getById(Integer id) {
        Optional<Post> post = postRepository.findById(id);
        String tags = "";
        if (post.isPresent()) {
            for (Tag tag : post.get().getTags()) {
                tags = tag.getName() + " ";
            }
            post.get().setTagString(tags);
        }
        return post;
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

    public List<Post> findByFiltering(String filterString, Set<Integer> postIds) {
        return postRepository.findByFiltering(filterString, postIds);
    }

    public void deletePostById(Integer id) {
        postRepository.deleteById(id);
    }
}