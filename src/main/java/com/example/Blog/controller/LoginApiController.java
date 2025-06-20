package com.example.Blog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/login")
public class LoginApiController {

    @GetMapping("/api/session")
    @PreAuthorize("isAuthenticated()")
    public Object sessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> res = new HashMap<>();
        if (Objects.nonNull(auth) && auth.isAuthenticated()) {
            res.put("success", Boolean.TRUE.toString());
            res.put("user", auth.getName());
            res.put("jSessionId", session.getId());
            res.put("x-csrf-token", request.getHeader("x-csrf-token"));
        } else {
            res.put("success", Boolean.FALSE.toString());
        }
        return res;
    }
}
