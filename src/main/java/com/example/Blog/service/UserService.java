package com.example.Blog.service;

import com.example.Blog.MyUserDetails;
import com.example.Blog.model.User;
import com.example.Blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public boolean registerUser(User user) {
        user.setRole("USER");
        boolean isAlreadyExist = userRepository.existsByUsername(user.getUsername());
        if (!isAlreadyExist) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }
        return new MyUserDetails(user);
    }

    public List<User> findAllAuthors() {
        List<String> authors = new ArrayList<>();
        authors.add("ADMIN");
        authors.add("AUTHOR");
        return userRepository.findAllByRole(authors);
    }

    public Optional<User> findAuthorById(int authorId) {
        return userRepository.findById(authorId);
    }

    public String findNameByEmail(String email) {
        return userRepository.findNameByUsername(email);
    }
}
