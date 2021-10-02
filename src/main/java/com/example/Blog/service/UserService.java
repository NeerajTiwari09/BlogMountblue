package com.example.Blog.service;

import com.example.Blog.MyUserDetails;
import com.example.Blog.model.Login;
import com.example.Blog.model.User;
import com.example.Blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public boolean registerUser(User user){
        User isRegistered = userRepository.save(user);
        if(isRegistered != null){
            return true;   
        }
        return false;
    }

//    public boolean checkUserAuthentication(Login login){
//        User user = userRepository.findByUsername(login.getUsername());
//        if(user != null){
//            if(user.getPassword().equals(login.getPassword())){
//                return true;
//            }
//        }
//        return  false;
//    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(s);
        if (user == null) {
            System.out.println("not");
            throw new UsernameNotFoundException("Could not find user");
        }
        return new MyUserDetails(user);
    }
}
