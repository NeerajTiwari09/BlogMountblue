package com.example.Blog.dto;

import com.example.Blog.model.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class UserDto {
    private Integer id;
    private String name;
    private String username;
    private String password;
    private String imageUrl = "https://picsum.photos/id/237/200/300";
    private List<Integer> following = new ArrayList<>();
    private List<Integer> followers = new ArrayList<>();
}
