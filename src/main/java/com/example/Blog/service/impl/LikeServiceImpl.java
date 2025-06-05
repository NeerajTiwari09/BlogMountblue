package com.example.Blog.service.impl;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.constant.NotificationUrl;
import com.example.Blog.dto.UserDto;
import com.example.Blog.dto.input_dto.LikeDto;
import com.example.Blog.dto.input_dto.SearchDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.enums.ErrorCode;
import com.example.Blog.enums.NotificationMessage;
import com.example.Blog.event.EventBuffer;
import com.example.Blog.event.EventForAuthor;
import com.example.Blog.model.Like;
import com.example.Blog.model.Post;
import com.example.Blog.model.User;
import com.example.Blog.repository.LikeRepository;
import com.example.Blog.repository.PostRepository;
import com.example.Blog.service.LikeService;
import com.example.Blog.service.MinioService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.Blog.constant.NotificationUrl.BLOG_URL_PATTERN;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private NotificationServiceImpl notificationServiceImpl;

    @Autowired
    private MinioService minioService;

    @Autowired
    private EventBuffer eventBuffer;

    @Override
    public Response<List<UserDto>> getAllUserByPostId(Integer postId) {
        User user = AuthProvider.getAuthenticatedUser();
        if (Objects.nonNull(user)) {
            List<UserDto> userDtos = new ArrayList<>();
            Optional<Post> post = postRepository.findById(postId);
            if (post.isPresent()) {
                List<Like> likes = likeRepository.findAllUserByPost(post.get());
                likes.forEach(like -> {
                    UserDto dto = new UserDto();
                    BeanUtils.copyProperties(like.getUser(), dto);
                    if (StringUtils.isNotEmpty(like.getUser().getImageKey())) {
                        dto.setImageUrl(minioService.downloadFileBase64(like.getUser().getImageKey()));
                    }
                    userDtos.add(dto);
                });
            }
            return new Response<>(userDtos);
        }
        return new Response<>(ErrorCode.LOGIN_REQUIRED);
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
                if (!post.get().getAuthor().getUsername().equals(user.getUsername())) {
                    eventBuffer.bufferLike(new EventForAuthor(post.get(), user, post.get().getAuthor()));
                }
            }
            Integer likesCount = likeRepository.countByPost(post.get());
            likeDto.setLikesCount(likesCount);
            return new Response<>(likeDto);
        }
        return new Response<>(ErrorCode.LOGIN_TO_LIKE_POST);
    }

    @Override
    public Response<List<UserDto>> getLazyLikes(SearchDto searchDto) {
        User user = AuthProvider.getAuthenticatedUser();
        if (Objects.nonNull(user)) {
            List<UserDto> userDtos = new ArrayList<>();
            Map<String, Object> query = searchDto.getQuery();
            if (Objects.nonNull(query.get("postId"))) {
                Optional<Post> post = postRepository.findById(Integer.parseInt((String) query.get("postId")));
                int offset = searchDto.getOffset();
                int limit = searchDto.getLimit();
                Sort.Direction sortDirection = Sort.Direction.DESC;
                Pageable pageable = PageRequest.of(offset - 1, limit, Sort.by(sortDirection, "id"));
                List<Like> likes = likeRepository.findAllUserByPost(post.get(), pageable);
                likes.forEach(like -> {
                    UserDto dto = new UserDto();
                    BeanUtils.copyProperties(like.getUser(), dto);
                    if (StringUtils.isNotEmpty(like.getUser().getImageKey())) {
                        dto.setImageUrl(minioService.downloadFileBase64(like.getUser().getImageKey()));
                    }
                    userDtos.add(dto);
                });
            }
            return new Response<>(userDtos);
        }
        return new Response<>(ErrorCode.LOGIN_REQUIRED);
    }
}
