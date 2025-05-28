package com.example.Blog.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SystemSetting {

    @Value("${password.min.length:8}")
    private int minPasswordLength;
}
