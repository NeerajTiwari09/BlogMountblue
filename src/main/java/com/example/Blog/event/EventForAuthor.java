package com.example.Blog.event;

import com.example.Blog.model.Post;
import com.example.Blog.model.User;

public record EventForAuthor(Post post, User user, User recipient) {
}
