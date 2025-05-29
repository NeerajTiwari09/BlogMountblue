package com.example.Blog.service.impl;

import com.example.Blog.service.MinioService;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

@Service
public class MinioServiceImpl implements MinioService {

    private final S3Client s3Client;
    private final String bucketName = "mybucket";

    public MinioServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
        if (!bucketExists()) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        }
    }

    @Override
    public boolean bucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public void uploadFile(String key, MultipartFile file) throws IOException {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );
    }

    @Override
    public byte[] downloadFile(String key) {
        try {
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build());
            return objectBytes.asByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public String downloadFileBase64(String key) {
        byte[] imgInByte = downloadFile(key);
        if (imgInByte.length != 0) {
            String base64 = Base64.getEncoder().encodeToString(imgInByte);
            return "data:image/jpeg;base64," + base64;
        }
        return null;
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }
}
