package com.example.Blog.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SystemSetting {

    @Value("${password.min.length:8}")
    private int minPasswordLength;
    @Value("${aws.access.key.id:admin}")
    private String accessKeyId;
    @Value("${aws.secret.access.key:admin123}")
    private String secretAccessKey;
    @Value("${aws.region:us-east-1}")
    private String awsRegion;
    @Value("${aws.url-path:http://localhost:9000}")
    private String awsUrl;
}
