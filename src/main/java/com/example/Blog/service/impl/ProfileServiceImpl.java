package com.example.Blog.service.impl;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.dto.PasswordDto;
import com.example.Blog.dto.UserDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.enums.ErrorCode;
import com.example.Blog.enums.SuccessCode;
import com.example.Blog.model.User;
import com.example.Blog.repository.UserRepository;
import com.example.Blog.service.MinioService;
import com.example.Blog.service.ProfileService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Objects;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MinioService minioService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Response<UserDto> updateProfile(String name, MultipartFile image) {
        try {
            User user = AuthProvider.getAuthenticatedUser();
            if (Objects.nonNull(user)) {
                if (StringUtils.hasText(name)) {
                    user.setName(name);
                }
                if (image != null && !image.isEmpty()) {
                    minioService.uploadFile(image.getOriginalFilename(), image);
                    user.setImageKey(image.getOriginalFilename());
                }
                userRepository.save(user);
                UserDto userDto = new UserDto();
                BeanUtils.copyProperties(user, userDto);
                if (StringUtils.hasText(user.getImageKey())) {
                    byte[] imageBytes = minioService.downloadFile(user.getImageKey());
                    String base64 = Base64.getEncoder().encodeToString(imageBytes);
                    String imageUrl = "data:image/jpeg;base64," + base64;
                    userDto.setImageUrl(imageUrl);
                }
                return new Response<>(userDto, SuccessCode.SUCCESSFUL_PROFILE_UPDATE);
            }
        } catch (Exception e) {
            return new Response<>(ErrorCode.ERROR_UPDATING_PROFILE);
        }
        return new Response<>(ErrorCode.LOGIN_REQUIRED);
    }

    @Override
    public Response<Object> updatePassword(PasswordDto passwordDto) {
        User user = AuthProvider.getAuthenticatedUser();
        if (Objects.nonNull(user)) {
            if (ObjectUtils.anyNull(passwordDto.getNewPassword(),
                    passwordDto.getNewPassword(), passwordDto.getConfirmPassword())) {
                return new Response<>(ErrorCode.ALL_PASSWORD_FIELDS_MANDATORY);
            }
            if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
                return new Response<>(ErrorCode.PASSWORD_NOT_MATCHED);
            }
            boolean matches = passwordEncoder.matches(passwordDto.getCurrentPassword(), user.getPassword());
            if (!matches) {
                return new Response<>(ErrorCode.INCORRECT_CURRENT_PASSWORD);
            }
            user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
            userRepository.save(user);
            return new Response<>(SuccessCode.SUCCESSFUL_PASSWORD_CHANGED);
        }
        return new Response<>(ErrorCode.LOGIN_REQUIRED);
    }
}
