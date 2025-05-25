package com.example.Blog.service;

import com.example.Blog.dto.PasswordDto;
import com.example.Blog.dto.UserDto;
import com.example.Blog.dto.output_dto.Response;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {

    Response<UserDto> updateProfile(String name, MultipartFile image);

    Response<Object> updatePassword(PasswordDto passwordDto);
}
