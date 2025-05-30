package com.example.Blog.controller;

import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.model.Login;
import com.example.Blog.model.User;
import com.example.Blog.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
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

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") User user, BindingResult result, Model model) {
        Response<Object> output = userService.registerUser(user);
        if (output.isSuccess()) {
            return "redirect:/login";
        }
        result.rejectValue("username", "error.user", output.getMessage());
        return "register";
    }
}
