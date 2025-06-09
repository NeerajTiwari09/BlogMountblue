package com.example.Blog.service.impl;

import com.example.Blog.MyUserDetails;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.enums.ErrorCode;
import com.example.Blog.enums.RoleName;
import com.example.Blog.model.Role;
import com.example.Blog.model.User;
import com.example.Blog.repository.RoleRepository;
import com.example.Blog.repository.UserRepository;
import com.example.Blog.utils.SystemSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SystemSetting systemSetting;

    @Autowired
    private LoginAttemptService loginAttemptService;

    public Response<Object> registerUser(User user) {
        Response<Object> output = validateInput(user);
        if (!output.isSuccess()) {
            return output;
        }
        Role role = roleRepository.findByName(RoleName.ROLE_AUTHOR);
        user.getRoles().add(role);
        String encode = passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
        userRepository.save(user);
        return new Response<>();

    }

    private Response<Object> validateInput(User user) {
        if (!StringUtils.hasText(user.getName())) {
            return new Response<>(ErrorCode.NAME_SHOULD_NOT_EMPTY);
        }
        if (!StringUtils.hasText(user.getUsername())) {
            return new Response<>(ErrorCode.EMAIL_SHOULD_NOT_EMPTY);
        }
        if (!StringUtils.hasText(user.getPassword()) ||
                user.getPassword().length() < systemSetting.getMinPasswordLength()) {
            return new Response<>(ErrorCode.INVALID_PASSWORD_LENGTH);
        }
        boolean isAlreadyExist = userRepository.existsByUsername(user.getUsername());
        if (isAlreadyExist) {
            return new Response<>(ErrorCode.EMAIL_ALREADY_IN_USE);
        }
        return new Response<>();
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if (loginAttemptService.isBlocked(s)) {
            throw new LockedException("Too many failed attempts. Account is locked for 5 minutes.");
        }
        User user = userRepository.findByUsername(s);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }
        return new MyUserDetails(user);
    }

    public List<User> findAllAuthors() {
        return userRepository.findAll();
    }

    public String findAuthorById(int authorId) {
        Optional<User> user = userRepository.findById(authorId);
        return user.isPresent() ? user.get().getName() : "";
    }

    public String findNameByEmail(String email) {
        return userRepository.findNameByUsername(email);
    }
}
