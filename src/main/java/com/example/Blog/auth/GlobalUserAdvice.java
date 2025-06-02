package com.example.Blog.auth;

import com.example.Blog.dto.UserDto;
import com.example.Blog.model.User;
import com.example.Blog.service.FollowService;
import com.example.Blog.service.MinioService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class GlobalUserAdvice {

    @Autowired
    private MinioService minioService;

    @Autowired
    private FollowService followService;

    @ModelAttribute("currentUser")
    public UserDto getCurrentUser() {
        User user = AuthProvider.getAuthenticatedUser();
        if (Objects.nonNull(user)) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user, userDto);
            userDto.setPassword(null);
            userDto.setImageUrl(null);
            List<Integer> followData = followService.getFollowingIds(user.getId());
            if (!followData.isEmpty()) {
                userDto.setFollowing(followData);
            }
            if (StringUtils.hasText(user.getImageKey())) {
                String imageUrl = minioService.downloadFileBase64(user.getImageKey());
                userDto.setImageUrl(imageUrl);
            }
            return userDto;
        }
        return null;
    }
}
