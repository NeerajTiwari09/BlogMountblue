package com.example.Blog.response;

import com.example.Blog.model.Comment;
import com.example.Blog.model.Post;

import java.util.List;

public class PostWithComment {
    private Post post;
    private List<Comment> comments;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
