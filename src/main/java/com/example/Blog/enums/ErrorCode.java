package com.example.Blog.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {

    EMAIL_ALREADY_IN_USE("Email already in use."),
    NAME_SHOULD_NOT_EMPTY("Name is required."),
    EMAIL_SHOULD_NOT_EMPTY("Email is required."),
    INVALID_PASSWORD_LENGTH("Too short — password must be 8+ characters."),
    LOGIN_TO_LIKE_POST("Please login to like the post."),
    INVALID_POST("Doesn't find the requested post."),
    ERROR_UPDATING_PROFILE("Something went wrong while updating profile."),
    LOGIN_REQUIRED("User need to login first."),
    FILE_SIZE_EXCEED("The field image exceeds its maximum permitted size of 1048576 bytes."),
    ALL_PASSWORD_FIELDS_MANDATORY("All password fields are mandatory."),
    INCORRECT_CURRENT_PASSWORD("Current password is incorrect."),
    PASSWORD_NOT_MATCHED("New password does not match with the confirm password.");

    private final String errorMessage;

    ErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
