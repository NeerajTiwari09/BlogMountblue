package com.example.Blog.service.impl;

import com.example.Blog.dto.NotificationDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.enums.NotificationType;
import com.example.Blog.model.Notification;
import com.example.Blog.model.User;
import com.example.Blog.repository.NotificationRepository;
import com.example.Blog.service.NotificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationServiceImpl(NotificationRepository repo, SimpMessagingTemplate messagingTemplate) {
        this.repo = repo;
        this.messagingTemplate = messagingTemplate;
    }

    @Async("notificationTaskExecutor")
    @Override
    public void sendNotification(User recipient, String message, String urlToRedirect) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setRecipient(recipient);
        notification.setNotificationType(NotificationType.ONLY_MESSAGE);
        if (StringUtils.isNotEmpty(urlToRedirect)) {
            notification.setUrlToRedirect(urlToRedirect);
            notification.setNotificationType(NotificationType.WITH_URL);
        }
        repo.save(notification);

        NotificationDto notificationDto = new NotificationDto();
        BeanUtils.copyProperties(notification, notificationDto);
        messagingTemplate.convertAndSendToUser(
                recipient.getUsername(),
                "/queue/notifications",
                notificationDto
        );
    }

    @Override
    public List<Notification> getAllNotifications(User user) {
        return repo.findByRecipient(user);
    }

    @Override
    public Response<Object> markAsRead(Long notificationId, User user) {
        NotificationDto dto = new NotificationDto();
        Notification notification = repo.findById(notificationId)
                .filter(n -> n.getRecipient().equals(user))
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (notification.isUnSeen()) {
            notification.setSeen(true);
            repo.save(notification);
            dto.setAlreadySeen(Boolean.FALSE);
        } else {
            dto.setAlreadySeen(Boolean.TRUE);
        }
        return new Response<>(dto);
    }
}
