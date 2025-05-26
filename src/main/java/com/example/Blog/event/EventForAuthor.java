package com.example.Blog.event;

import com.example.Blog.model.Post;
import com.example.Blog.model.User;
import lombok.Data;

@Data
public class EventForAuthor {
    private final Post post;
    private final User user;
    private final User postOwner;

}
