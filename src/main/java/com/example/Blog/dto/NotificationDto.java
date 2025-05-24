package com.example.Blog.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDto {
    private String message;
    private boolean seen;
    private LocalDateTime createdAt;
}
