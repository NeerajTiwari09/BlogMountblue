package com.example.Blog.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MinioService {

    boolean bucketExists();

    void uploadFile(String key, MultipartFile file) throws IOException;

    byte[] downloadFile(String key);

    void deleteFile(String key);
}
