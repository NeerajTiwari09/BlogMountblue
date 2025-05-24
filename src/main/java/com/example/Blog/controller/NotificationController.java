package com.example.Blog.controller;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.model.User;
import com.example.Blog.service.impl.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{id}/read")
    public Response<Object> readNotification(@PathVariable Long id) {
        User user = AuthProvider.getAuthenticatedUser();
        notificationService.markAsRead(id, user);
        return new Response<>();
    }

}
