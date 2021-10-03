package com.example.Blog.controller;

import com.example.Blog.model.Login;
import com.example.Blog.model.User;
import com.example.Blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String getLoginView(Model model){
        Login login = new Login();
        model.addAttribute("login", login);
        return "login";
    }

    @GetMapping("/register")
    public String getRegistrationPage(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "register";
    }

    @RequestMapping(value = "/register" , method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") User user){
        boolean isRegistered = userService.registerUser(user);
        if(isRegistered){
            return "redirect:/login";
        }
        return "register";
    }

//    @PostMapping("/authenticateUser")
//    public String login(@ModelAttribute("login") Login login){
////        boolean isauthorised = userService.checkUserAuthentication(login);
////        System.out.println(isauthorised);
//        return "login";
//    }
}
