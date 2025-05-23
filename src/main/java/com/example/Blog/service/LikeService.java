package com.example.Blog.service;

import com.example.Blog.dto.input_dto.LikeDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.model.Post;
import com.example.Blog.model.User;

public interface LikeService {
    Integer getLikeCountByPostId(Integer postId);
    Integer getLikeCountByPost(Post post);
    boolean likedByCurrentUser(Post post, User user);
    Response<LikeDto> toggleLike(Integer postId);
}
