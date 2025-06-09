package com.example.Blog.exception_handler;

import com.example.Blog.enums.ErrorCode;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
    }
}
