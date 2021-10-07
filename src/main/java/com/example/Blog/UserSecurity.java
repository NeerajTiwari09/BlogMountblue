package com.example.Blog;

import com.example.Blog.model.Post;
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

    public boolean hasUserId(Authentication authentication, Integer postId) {
        Optional<Post> post = postRepository.findById(postId);
        String authorName = userRepository.findByUsername(authentication.getName()).getName();
        if(post.get().getAuthor().equals(authorName) && post.isPresent()){
            return true;
        }
        return false;
    }
}