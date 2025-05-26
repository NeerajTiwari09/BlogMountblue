package com.example.Blog.controller;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.model.Notification;
import com.example.Blog.model.User;
import com.example.Blog.service.NotificationService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class HeaderController {

    private final NotificationService notificationService;

    public HeaderController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ModelAttribute
    public void addNotifications(Model model) {
        User user = AuthProvider.getAuthenticatedUser();
        if (Objects.nonNull(user)) {
            List<Notification> allNotifications = notificationService.getAllNotifications(user);
            allNotifications.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
            Long unread = allNotifications.stream().filter(Notification::isUnSeen).count();
            model.addAttribute("unreadNotifications", allNotifications);
            model.addAttribute("unreadCount", unread);
        }
    }
}
