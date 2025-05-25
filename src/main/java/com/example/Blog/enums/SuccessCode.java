package com.example.Blog.enums;

public enum SuccessCode {
    BLOG_DELETED_SUCCESSFUL("Blog deleted successfully."),
    SUCCESSFUL_PROFILE_UPDATE("Profile updated successfully."),
    SUCCESSFUL_PASSWORD_CHANGED("Password changed successfully.");

    private final String message;

    SuccessCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
