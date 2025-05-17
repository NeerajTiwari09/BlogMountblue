package com.example.Blog.enums;

public enum ErrorCode {

    EMAIL_ALREADY_IN_USE("Email already in use."),
    NAME_SHOULD_NOT_EMPTY("Name should not be empty.");

    private final String errorMessage;

    ErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
