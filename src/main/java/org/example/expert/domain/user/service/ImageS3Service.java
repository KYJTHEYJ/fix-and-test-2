package org.example.expert.domain.user.service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageS3Service {
    private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofHours(12L); // 7일 유지

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private final Long maxSize = 10 * 1024 * 1024L; // 10MB
    private final List<String> availableFileTypeList = List.of("image/jpeg", "image/jpg", "image/png");

    public String getUploadKey(MultipartFile file) {
        if(file.getSize() > maxSize) {
            throw new InvalidRequestException("File is too large");
        }

        if(!availableFileTypeList.contains(file.getContentType()) ) {
            throw new InvalidRequestException("File is not available");
        }

        try {
            String imageKey = "profile-image/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            s3Template.upload(bucket, imageKey, file.getInputStream());
            return imageKey;
        } catch (IOException e) {
            throw new InvalidRequestException("Error while uploading file");
        }
    }

    public URL getDownloadUrl(String imageKey) {
        return s3Template.createSignedGetURL(bucket, imageKey, PRESIGNED_URL_EXPIRATION);
    }
}
