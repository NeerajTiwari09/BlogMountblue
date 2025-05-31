package com.example.Blog.service;

import com.example.Blog.dto.input_dto.SearchDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.model.Post;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SavePostService {

    Response<Object> toggleSavePostForUser(Integer postId);

    Page<Post> getSavedPostForUser(SearchDto searchDto);

    List<Long> getSavedPostIdForUser();
}
