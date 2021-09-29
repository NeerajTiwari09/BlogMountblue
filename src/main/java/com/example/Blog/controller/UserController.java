package com.example.Blog.controller;

import com.example.Blog.model.Login;
import com.example.Blog.model.User;
import com.example.Blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login")
    public String getLoginView(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegistrationView(Model model){
        User user = new User();
        model.addAttribute("user", user);
        System.out.println("GET");
        return "register";
    }

    @RequestMapping("/register")
    public String registerUser(@ModelAttribute("user") User user){
        boolean isRegistered = userService.registerUser(user);
        System.out.println("Post");
        if(isRegistered){
            return "login";
        }
        return "register";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@ModelAttribute("login") Login login){
        userService.checkUserAuthentication(login);
        return "first";
    }
}
