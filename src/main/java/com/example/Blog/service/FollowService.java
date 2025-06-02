package com.example.Blog.service;

import com.example.Blog.dto.output_dto.Response;

import java.util.List;

public interface FollowService {

    Response<Object> toggleFollowUser(Integer authorId);

    List<Integer> getFollowingIds(Integer id);
}
