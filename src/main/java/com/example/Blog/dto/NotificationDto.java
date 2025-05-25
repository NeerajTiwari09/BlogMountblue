package com.example.Blog.dto;

import com.example.Blog.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDto {
    private String message;
    private boolean seen;
    private LocalDateTime createdAt;
    private NotificationType notificationType;
}
