package com.example.Blog.service;

import com.example.Blog.dto.UserDto;
import com.example.Blog.dto.input_dto.LikeDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.model.Post;
import com.example.Blog.model.User;

import java.util.List;

public interface LikeService {
    Response<List<UserDto>> getAllUserByPostId(Integer postId);
    Integer getLikeCountByPost(Post post);
    boolean likedByCurrentUser(Post post, User user);
    Response<LikeDto> toggleLike(Integer postId);
}
