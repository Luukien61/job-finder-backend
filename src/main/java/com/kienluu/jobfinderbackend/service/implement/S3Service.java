package com.kienluu.jobfinderbackend.service.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.bucket-name}")
    private String bucketName;
    @Value("${aws.region}")
    private String region;

    public String uploadFile(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            GetUrlRequest urlRequest = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .region(Region.AP_SOUTHEAST_1)
                    .build();

            URL url = s3Client.utilities().getUrl(urlRequest);
            log.info(url.toString());
            return url.toString();
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    public void deleteFile(String fileName) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (NoSuchKeyException e) {
            throw new RuntimeException("File not found: " + fileName, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    public boolean exists(String fileName) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    public void deleteFileFromS3ByUrl(String fileUrl) {
        if (fileUrl.isEmpty()) {
            throw new IllegalArgumentException("File URL cannot be empty");
        }

        try {
            String key = getS3KeyFromUrl(fileUrl);
            log.info("Attempting to delete file with key: {}", key);

            if (exists(key)) {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();

                s3Client.deleteObject(deleteObjectRequest);
                log.info("Successfully deleted file: {}", key);
            } else {
                log.warn("File does not exist: {}", key);
                throw new FileNotFoundException("File not found in S3: " + key);
            }
        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", fileUrl, e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    private String getS3KeyFromUrl(String fileUrl) {
        try {
            String decodedUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8);

            String key;

            if (decodedUrl.contains("amazonaws.com")) {
                // Pattern 1: https://bucket-name.s3.region.amazonaws.com/key
                // Pattern 2: https://s3.region.amazonaws.com/bucket-name/key
                URL url = new URL(decodedUrl);
                String path = url.getPath();

//                if (path.startsWith("/" + bucketName + "/")) {
//                    // Pattern 2
//                    key = path.substring(bucketName.length() + 2);
//                } else
                if (path.startsWith("/")) {
                    key = path.substring(1);
                } else {
                    key = path;
                }
            } else if (decodedUrl.startsWith("/")) {
                // Pattern 3: /folder/file.jpg
                key = decodedUrl.substring(1);
            } else {
                // Pattern 4: folder/file.jpg
                key = decodedUrl;
            }

            // Cleanup key
            key = key.trim()
                    .replace("\\", "/")
                    .replaceAll("/{2,}", "/"); // Remove multiple consecutive slashes

            log.info("Extracted key '{}' from URL '{}'", key, fileUrl);
            return key;

        } catch (Exception e) {
            log.error("Error parsing S3 URL: {}", fileUrl, e);
            throw new IllegalArgumentException("Invalid S3 URL format: " + fileUrl);
        }
    }
}
