package com.example.Blog.config;

import com.example.Blog.service.impl.UserService;
import com.example.Blog.utils.CustomAuthenticationFailureHandler;
import com.example.Blog.utils.CustomAuthenticationSuccessHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/posts/search", "/register", "/comment").permitAll()
                .antMatchers("/posts/new", "/posts/update").hasAnyAuthority("ADMIN", "AUTHOR")
                .antMatchers("/posts/publish").hasAnyAuthority("ADMIN", "AUTHOR")
                .and()
                .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/authenticateUser")
                    .failureHandler(customAuthenticationFailureHandler)
                    .successHandler(customAuthenticationSuccessHandler)
                    .permitAll()
                .and()
                    .logout()
                    .permitAll();
        http.exceptionHandling().accessDeniedPage("/access-denied");
    }
}
