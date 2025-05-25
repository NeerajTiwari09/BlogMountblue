package com.example.Blog.dto;

import lombok.Data;

@Data
public class UserDto {
    private Integer id;
    private String name;
    private String username;
    private String password;
    private String imageUrl = "https://picsum.photos/id/237/200/300";
}
