package com.example.Blog.service.impl;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.dto.input_dto.LikeDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.enums.ErrorCode;
import com.example.Blog.model.Like;
import com.example.Blog.model.Post;
import com.example.Blog.model.User;
import com.example.Blog.repository.LikeRepository;
import com.example.Blog.repository.PostRepository;
import com.example.Blog.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Override
    public Integer getLikeCountByPostId(Integer postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (Objects.nonNull(post)) {
            return likeRepository.countByPost(post);
        }
        return 0;
    }

    @Override
    public Integer getLikeCountByPost(Post post) {
        return likeRepository.countByPost(post);
    }

    @Override
    public boolean likedByCurrentUser(Post post, User user) {
        return likeRepository.findByPostAndUser(post, user).isPresent();
    }

    @Override
    public Response<LikeDto> toggleLike(Integer postId) {
        User user = AuthProvider.getAuthenticatedUser();
        Optional<Post> post = postRepository.findById(postId);
        LikeDto likeDto = new LikeDto();
        if (Objects.nonNull(user) && post.isPresent()) {
            Optional<Like> existing = likeRepository.findByPostAndUser(post.get(), user);
            if (existing.isPresent()) {
                likeRepository.deleteById(existing.get().getId());
                likeDto.setLiked(Boolean.FALSE);
            } else {
                Like like = new Like();
                like.setUser(user);
                like.setPost(post.get());
                likeDto.setLiked(Boolean.TRUE);
                likeRepository.save(like);
            }
            Integer likesCount = likeRepository.countByPost(post.get());
            likeDto.setLikesCount(likesCount);
            return new Response<>(likeDto);
        }
        return new Response<>(ErrorCode.LOGIN_TO_LIKE_POST);
    }
}
