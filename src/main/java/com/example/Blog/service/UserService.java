package com.example.Blog.service;

import com.example.Blog.MyUserDetails;
import com.example.Blog.model.Role;
import com.example.Blog.model.User;
import com.example.Blog.repository.RoleRepository;
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

    @Autowired
    private RoleRepository roleRepository;

    public boolean registerUser(User user) {
        user.setRole("AUTHOR");
        Role role = roleRepository.findByName("AUTHOR");
        user.getRoles().add(role);
        boolean isAlreadyExist = userRepository.existsByUsername(user.getUsername());
        if (!isAlreadyExist) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(s);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }
        return new MyUserDetails(user);
    }

    public List<User> findAllAuthors() {
        return userRepository.findAll();
    }

    public Optional<User> findAuthorById(int authorId) {
        return userRepository.findById(authorId);
    }

    public String findNameByEmail(String email) {
        return userRepository.findNameByUsername(email);
    }
}
