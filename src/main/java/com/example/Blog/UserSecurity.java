package com.example.Blog;

import com.example.Blog.model.Comment;
import com.example.Blog.model.Post;
import com.example.Blog.repository.CommentRepository;
import com.example.Blog.repository.PostRepository;
import com.example.Blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("userSecurity")
public class UserSecurity {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    public boolean hasUserId(Authentication authentication, Integer postId) {
        Optional<Post> post = postRepository.findById(postId);
        String authorName = userRepository.findByUsername(authentication.getName()).getName();
        return post.isPresent() && post.get().getAuthor().equals(authorName);
    }

    public boolean hasCommentId(Authentication authentication, Integer commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        String email = authentication.getName();
        return comment.isPresent() && comment.get().getEmail().equals(email);
    }
}