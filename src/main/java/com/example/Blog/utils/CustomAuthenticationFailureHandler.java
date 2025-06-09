package com.example.Blog.utils;

import com.example.Blog.service.impl.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
                                        throws IOException, ServletException {

        String errorMessage;
        String username = request.getParameter("username");
        if (exception instanceof BadCredentialsException) {
            errorMessage = "Invalid username or password";
            loginAttemptService.loginFailed(username);
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = "User does not exist";
        } else if (exception instanceof LockedException) {
            errorMessage = "Your account is locked";
        } else {
            errorMessage = "Login failed: " + exception.getMessage();
        }

        // You can redirect to a URL with error param
        response.sendRedirect("/blog/login?error=" + errorMessage);
    }
}
