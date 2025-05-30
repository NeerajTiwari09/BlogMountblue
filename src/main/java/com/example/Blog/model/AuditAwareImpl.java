package com.example.Blog.model;


import com.example.Blog.auth.AuthProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component("auditorProvider")
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        User user = AuthProvider.getAuthenticatedUser();
        if (Objects.nonNull(user)) {
            return Optional.of(user.getUsername());
        }
        return Optional.empty();
    }

}
