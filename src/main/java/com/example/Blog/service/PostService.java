package com.example.Blog.service;

import com.example.Blog.model.Post;
import com.example.Blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public List<Post> listAll() {
        return postRepository.findAll();
    }

    public void save(Post post) {
        postRepository.save(post);
    }

    public Optional<Post> get(Integer id) {
        return postRepository.findById(id);
    }

    public void delete(Integer id) {
        postRepository.deleteById(id);
    }
}
