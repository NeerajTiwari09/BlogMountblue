package com.example.Blog.dto.output_dto;

import com.example.Blog.enums.ErrorCode;
import lombok.Data;

@Data
public class Response<T> {
    private boolean success;
    private T data;
    private String message;
    private ErrorCode errorCode;

    public Response() {
        this.success = true;
    }

    public Response(T data) {
        this.success = true;
        this.data = data;
    }

    public Response(T data, String message) {
        this.success = true;
        this.data = data;
        this.message = message;
    }

    public Response(ErrorCode errorCode) {
        this.success = false;
        this.errorCode = errorCode;
        this.message = errorCode.getErrorMessage();
    }

}
