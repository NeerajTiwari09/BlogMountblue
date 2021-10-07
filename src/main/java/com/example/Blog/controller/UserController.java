package com.example.Blog.controller;

import com.example.Blog.model.Login;
import com.example.Blog.model.User;
import com.example.Blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String viewLoginPage(Model model) {
        Login login = new Login();
        model.addAttribute("login", login);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";
    }

    @GetMapping("/register")
    public String viewRegistrationPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        boolean isRegistered = userService.registerUser(user);
        if (isRegistered) {
            return "User registered";
        }
        return "Something error";
    }

    @GetMapping("/access-denied")
    public String showAccessDeniedPage() {
        return "access-denied";
    }

}
