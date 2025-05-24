package com.example.Blog.service.impl;

import com.example.Blog.dto.NotificationDto;
import com.example.Blog.model.Notification;
import com.example.Blog.model.User;
import com.example.Blog.repository.NotificationRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repo;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository repo, SimpMessagingTemplate messagingTemplate) {
        this.repo = repo;
        this.messagingTemplate = messagingTemplate;
    }

    @Async
    public void sendNotification(User recipient, String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setRecipient(recipient);
        repo.save(notification);

        NotificationDto notificationDto = new NotificationDto();
        BeanUtils.copyProperties(notification, notificationDto);
        messagingTemplate.convertAndSendToUser(
                recipient.getUsername(),
                "/queue/notifications",
                notificationDto
        );
    }

    public List<Notification> getAllNotifications(User user) {
        return repo.findByRecipient(user);
    }

    public void markAsRead(Long notificationId, User user) {
        Notification notification = repo.findById(notificationId)
                .filter(n -> n.getRecipient().equals(user))
                .orElseThrow(() -> new RuntimeException("Not found"));

        notification.setSeen(true);
        repo.save(notification);
    }
}
