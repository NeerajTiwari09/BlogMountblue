package com.example.Blog.auth;

import com.example.Blog.MyUserDetails;
import com.example.Blog.model.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;


public class AuthProvider {

    public static Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(auth) && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            if (auth.getPrincipal() instanceof MyUserDetails) {
                return ((MyUserDetails) auth.getPrincipal()).getUser();
            }
        }
        return null;
    }
}
