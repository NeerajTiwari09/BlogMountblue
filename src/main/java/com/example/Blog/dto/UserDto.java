package com.example.Blog.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDto {
    private Integer id;
    private String name;
    private String username;
    private String password;
    private String imageUrl;
    private List<Integer> following = new ArrayList<>();
    private List<Integer> followers = new ArrayList<>();
}
