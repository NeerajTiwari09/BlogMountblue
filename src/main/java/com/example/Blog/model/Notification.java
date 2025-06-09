package com.example.Blog.model;

import com.example.Blog.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "notification")
public class Notification extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "message")
    private String message;

    @Column(name = "seen")
    private boolean seen = false;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Column(name = "notification_type")
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(name = "url_to_redirect")
    private String urlToRedirect;

    public boolean isUnSeen() {
        return !this.seen;
    }
}
