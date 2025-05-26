package com.example.Blog.service;

import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.model.Notification;
import com.example.Blog.model.User;

import java.util.List;

public interface NotificationService {

    void sendNotification(User recipient, String message, String urlToRedirect);

    Response<Object> markAsRead(Long notificationId, User user);

    List<Notification> getAllNotifications(User user);
}
