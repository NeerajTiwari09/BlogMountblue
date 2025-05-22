package com.example.Blog.service;

import com.example.Blog.dto.input_dto.PostDto;
import com.example.Blog.dto.input_dto.SearchDto;
import com.example.Blog.model.Post;
import com.example.Blog.model.Tag;
import com.example.Blog.model.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Page<Post> getAllBlogs(int start, int limit);

    Optional<Post> getById(Integer id);

    Post publishPost(PostDto postDto);

    void deletePostById(Integer id);

    Page<Post> getAllBlogsByAuthor(User user, SearchDto searchDto);

    Page<Post> findByFilterCriteria(SearchDto searchDto);

    Page<Post> getBySearchString(SearchDto searchDto);

    List<Tag> getCurrentAuthorTags();
}
