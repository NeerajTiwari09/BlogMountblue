package com.example.Blog.enums;

import lombok.Getter;

@Getter
public enum SuccessCode {
    BLOG_DELETED_SUCCESSFUL("Blog deleted successfully."),
    SUCCESSFUL_PROFILE_UPDATE("Profile updated successfully."),
    SUCCESSFUL_PASSWORD_CHANGED("Password changed successfully."),
    SAVED_TO_LIBRARY("Saved to your library."),
    REMOVED_FROM_LIBRARY("Removed from your library."),
    FOLLOW_SUCCESSFULLY("User has been followed successfully."),
    UNFOLLOW_SUCCESSFULLY("User has been unfollowed successfully.");

    private final String message;

    SuccessCode(String message) {
        this.message = message;
    }

}
