package com.example.Blog.exception_handler;

import com.example.Blog.dto.output_dto.Response;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(FileSizeLimitExceededException.class)
    @ResponseBody
    public Response<Object> handleFileSizeLimitException(FileSizeLimitExceededException e) {
//        return new Response<>(ErrorCode.FILE_SIZE_EXCEED);
        Response<Object> response = new Response<>();
        response.setSuccess(Boolean.FALSE);
        response.setMessage(e.getMessage());
        return response;
    }
}
