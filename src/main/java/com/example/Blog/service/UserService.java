package com.example.Blog.service;

import com.example.Blog.model.Login;
import com.example.Blog.model.User;
import com.example.Blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public boolean registerUser(User user){
        User isRegistered = userRepository.save(user);
        if(isRegistered != null){
            return true;   
        }
        return false;
    }

    public boolean checkUserAuthentication(Login login){
        User user = userRepository.findByEmail(login.getEmail());
        if(user != null){
            if(user.getPassword().equals(login.getPassword())){
                return true;
            }
        }
        return  false;
    }
}
