package com.example.Blog.enums;

import lombok.Getter;

@Getter
public enum SuccessCode {
    BLOG_DELETED_SUCCESSFUL("Blog deleted successfully."),
    SUCCESSFUL_PROFILE_UPDATE("Profile updated successfully."),
    SUCCESSFUL_PASSWORD_CHANGED("Password changed successfully."),
    SAVED_TO_LIBRARY("Saved to your library."),
    REMOVED_FROM_LIBRARY("Removed from your library.");

    private final String message;

    SuccessCode(String message) {
        this.message = message;
    }

}
