package com.example.Blog.controller;

import com.example.Blog.constant.ToastConstant;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String viewLoginPage(@RequestParam(value = "error", required = false, defaultValue = "") String error,
                                Model model) {
        Login login = new Login();
        model.addAttribute("login", login);
        if (StringUtils.hasText(error)){
            model.addAttribute(ToastConstant.TOAST_MESSAGE, error);
            model.addAttribute(ToastConstant.TOAST_STATUS_COLOR, ToastConstant.TOAST_BG_DANGER);
        }
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
    public String registerUser(@ModelAttribute("user") User user, Model model, RedirectAttributes redirectAttributes) {
        Response<Object> output = userService.registerUser(user);
        if (output.isSuccess()) {
            redirectAttributes.addFlashAttribute(ToastConstant.TOAST_MESSAGE, "Registered successfully!");
            redirectAttributes.addFlashAttribute(ToastConstant.TOAST_STATUS_COLOR, ToastConstant.TOAST_BG_SUCCESS);
            return "redirect:/login";
        }
        model.addAttribute(ToastConstant.TOAST_MESSAGE, output.getMessage());
        model.addAttribute(ToastConstant.TOAST_STATUS_COLOR, ToastConstant.TOAST_BG_DANGER);
        return "register";
    }
}
