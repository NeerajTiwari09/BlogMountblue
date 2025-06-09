package com.example.Blog.service.impl;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.dto.input_dto.ToggleDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.enums.ErrorCode;
import com.example.Blog.enums.SuccessCode;
import com.example.Blog.event.EventBuffer;
import com.example.Blog.event.EventForAuthor;
import com.example.Blog.exception_handler.UserNotFoundException;
import com.example.Blog.model.Follows;
import com.example.Blog.model.User;
import com.example.Blog.repository.FollowsRepository;
import com.example.Blog.repository.UserRepository;
import com.example.Blog.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowsRepository followsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventBuffer eventBuffer;

    @Transactional
    public Response<Object> toggleFollowUser(Integer followingId) {
        ToggleDto toggleDto = new ToggleDto();
        User follower = AuthProvider.getAuthenticatedUser();
        if (Objects.nonNull(follower)) {
            User following = userRepository.findById(followingId).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
            Optional<Follows> present = followsRepository.findByFollowerIdAndFollowingId(follower.getId(), followingId);
            if (present.isPresent()) {
                followsRepository.deleteById(present.get().getId());
                toggleDto.setToggled(Boolean.FALSE);
                return new Response<>(toggleDto, SuccessCode.UNFOLLOW_SUCCESSFULLY);
            } else {
                Follows follows = new Follows();
                follows.setFollowerId(follower.getId());
                follows.setFollowingId(following.getId());
                followsRepository.save(follows);
                toggleDto.setToggled(Boolean.TRUE);
                if (!follower.getId().equals(following.getId())) {
                    eventBuffer.bufferFollow(new EventForAuthor(null, follower, following));
                }
                return new Response<>(toggleDto, SuccessCode.FOLLOW_SUCCESSFULLY);
            }
        }
        return new Response<>(ErrorCode.LOGIN_REQUIRED);
    }

    public List<Follows> getFollowers(Integer userId) {
        return followsRepository.findByFollowingId(userId);
    }

    public List<Integer> getFollowingIds(Integer userId) {
        return followsRepository.findAllFollowingIdByFollowerId(userId);
    }

    public boolean isFollowing(Integer followerId, Integer followingId) {
        return followsRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }
}
