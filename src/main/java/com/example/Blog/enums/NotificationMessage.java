package com.example.Blog.enums;

public enum NotificationMessage {
    NEW_BLOG_POST("%s has posted a new blog."),
    COMMENT_ON_BLOG("%s commented on your blog \"%s\"."),
    LIKE_BLOG("%s liked your blog \"%s\".");

    private final String messageTemplate;

    NotificationMessage(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}
