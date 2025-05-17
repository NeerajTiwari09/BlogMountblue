package com.example.Blog.auth;

import com.example.Blog.MyUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalUserAdvice {
    @ModelAttribute("currentUser")
    public MyUserDetails getCurrentUser(@AuthenticationPrincipal MyUserDetails userDetails) {
        return userDetails;
    }
}
