package com.example.Blog.repository;

import com.example.Blog.model.Notification;
import com.example.Blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient(User recipient);
}
