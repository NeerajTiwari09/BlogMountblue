package com.example.Blog.dto.input_dto;

import com.example.Blog.dto.UserDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Integer id;
    private UserDto commenter;
    private String name;
    private String comment;
    private Integer postId;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}