package com.example.Blog.dto.output_dto;

import com.example.Blog.enums.ErrorCode;
import com.example.Blog.enums.SuccessCode;
import lombok.Data;

@Data
public class Response<T> {
    private boolean success;
    private T data;
    private String message;
    private ErrorCode errorCode;
    private SuccessCode successCode;

    public Response() {
        this.success = true;
    }

    public Response(T data) {
        this.success = true;
        this.data = data;
    }

    public Response(T data, SuccessCode successCode) {
        this.success = true;
        this.data = data;
        this.message = successCode.getMessage();
        this.successCode = successCode;
    }

    public Response(ErrorCode errorCode) {
        this.success = false;
        this.errorCode = errorCode;
        this.message = errorCode.getErrorMessage();
    }

    public Response(SuccessCode successCode) {
        this.success = true;
        this.successCode = successCode;
        this.message = successCode.getMessage();
    }

}
