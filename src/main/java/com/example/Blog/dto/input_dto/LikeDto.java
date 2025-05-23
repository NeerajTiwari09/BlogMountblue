package com.example.Blog.dto.input_dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeDto {
    private Integer postId;
    private boolean liked;
    private Integer likesCount;
}
