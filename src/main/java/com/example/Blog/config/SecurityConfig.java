package com.example.Blog.config;

import com.example.Blog.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        return authProvider;
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.authorizeRequests()
                .antMatchers("/", "/id", "/sort", "/search", "/register", "/filter").permitAll()
                .antMatchers("/comment", "/updateComment", "/deleteComment").hasAnyAuthority("ADMIN", "AUTHOR", "USER")
                .antMatchers("/blog/new", "/blog/update").hasAnyAuthority("ADMIN", "AUTHOR")
                .antMatchers("/blog/publish").hasAnyAuthority("ADMIN", "AUTHOR")
                .antMatchers(HttpMethod.DELETE, "/blog/delete/{postId}").access("@userSecurity.hasUserId(authentication, #postId)")
                .antMatchers(HttpMethod.GET, "/blog/update/{postId}").access("@userSecurity.hasUserId(authentication, #postId)")
                .antMatchers(HttpMethod.GET, "/updateComment/{commentId}").access("@userSecurity.hasCommentId(authentication, #commentId)")
                .antMatchers(HttpMethod.DELETE, "/deleteComment/{commentId}").access("@userSecurity.hasCommentId(authentication, #commentId)")
                .and()
                .httpBasic()
                .and()
                .formLogin()
                .loginProcessingUrl("/authenticateUser")
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
                .logout()
                .permitAll();
        http.exceptionHandling().accessDeniedPage("/access-denied");
    }
}