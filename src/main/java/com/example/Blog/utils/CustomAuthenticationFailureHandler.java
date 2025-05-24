package com.example.Blog.utils;

import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
                                        throws IOException, ServletException {

        String errorMessage;
        if (exception instanceof BadCredentialsException) {
            errorMessage = "Invalid username or password";
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
