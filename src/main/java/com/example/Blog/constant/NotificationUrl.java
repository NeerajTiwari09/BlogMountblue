package com.example.Blog.constant;

public class NotificationUrl {

    public static final String BLOG_URL_PATTERN = "/blog/posts/%s";

    public static String getUrl(String urlPattern, Object... args) {
        return String.format(urlPattern, args);
    }
}
