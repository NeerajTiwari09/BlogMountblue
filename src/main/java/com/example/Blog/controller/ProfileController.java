package com.example.Blog.controller;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.dto.PasswordDto;
import com.example.Blog.dto.UserDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.model.User;
import com.example.Blog.service.MinioService;
import com.example.Blog.service.ProfileService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
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
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user, userDto);
            userDto.setImageUrl(null);
            if (StringUtils.hasText(user.getImageKey())) {
                byte[] imageBytes = minioService.downloadFile(user.getImageKey());
                String base64 = Base64.getEncoder().encodeToString(imageBytes);
                String imageUrl = "data:image/jpeg;base64," + base64;
                userDto.setImageUrl(imageUrl);
            }
            model.addAttribute("user", userDto);
            return "profile";
        }
        return "redirect:/login";
    }

    @PostMapping("/update")
    @ResponseBody
    public Response<UserDto> updateProfile(@RequestParam String name, @RequestParam(required = false) MultipartFile image) {
        return profileService.updateProfile(name, image);
    }

    @PostMapping(value = "/update-password", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Response<Object> changePassword(@RequestBody PasswordDto passwordDto) {
        return profileService.updatePassword(passwordDto);
    }
}
