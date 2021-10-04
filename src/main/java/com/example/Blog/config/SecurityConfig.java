package com.example.Blog.config;

import com.example.Blog.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/id", "/sort", "/search", "/register", "/filter").permitAll()
                .antMatchers("/blog/new", "/blog/update").hasAnyAuthority("ADMIN", "AUTHOR")
                .antMatchers("/blog/publish").hasAnyAuthority("ADMIN", "AUTHOR")
                .and()
                .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/authenticateUser")
                    .permitAll()
                .and()
                    .logout()
                    .permitAll();
        http.exceptionHandling().accessDeniedPage("/access-denied");
    }
}
