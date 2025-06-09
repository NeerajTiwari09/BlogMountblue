package com.example.Blog.controller;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.dto.PasswordDto;
import com.example.Blog.dto.UserDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.model.User;
import com.example.Blog.service.MinioService;
import com.example.Blog.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private MinioService minioService;

    @GetMapping
    public String showProfile(Model model) {
        User user = AuthProvider.getAuthenticatedUser();
        if (Objects.nonNull(user)) {
            return "profile";
        }
        return "redirect:/login";
    }

    @PostMapping("/update")
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public Response<UserDto> updateProfile(@RequestParam String name, @RequestParam(required = false) MultipartFile image) {
        return profileService.updateProfile(name, image);
    }

    @PostMapping(value = "/update-password", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public Response<Object> changePassword(@RequestBody PasswordDto passwordDto) {
        return profileService.updatePassword(passwordDto);
    }
}
