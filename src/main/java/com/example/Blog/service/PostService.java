package com.example.Blog.service;

import com.example.Blog.model.Post;
import com.example.Blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public Page<Post> getAllBlogs(int start, int limit){
        Pageable firstPageWithTwoElements = PageRequest.of(start, limit);
        return postRepository.findAll(firstPageWithTwoElements);
    }

    public List<Post> listAll() {
        return postRepository.findAll();
    }

    public void save(Post post) {
        post.setAuthor("Mahindra");
        post.setPublished(true);
        post.setPublishedAt(new Timestamp(System.currentTimeMillis()));
        post.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        post.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        postRepository.save(post);
    }

    public Optional<Post> getById(Integer id) {
        return postRepository.findById(id);
    }

    public void delete(Integer id) {
        postRepository.deleteById(id);
    }

    public List<Post> getBySearchString(String searchString){
        return postRepository.getBySearchString(searchString);
    }

//    public List<Post> sort(String sortField, String order) {
//        return postRepository.sort(sortField, order);
//    }
    public  List<Post> findByOrderByPublishedAtAcs(){
        return postRepository.findByOrderByPublishedAtAsc();
    }
}
