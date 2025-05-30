package com.example.localstack.usecase.s3.crud;

import com.example.localstack.config.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for performing S3 file operations (upload, download, delete, list).
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Properties s3Properties;


    /**
     * Uploads a file to the S3 bucket.
     *
     * @param file The MultipartFile to upload.
     * @return A message indicating success or failure.
     */
    public String uploadFile(MultipartFile file) {
        // Ensure the bucket exists. In Localstack, it might not be created by Terraform if you run the app first.
        // For AWS, Terraform handles this.
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(s3Properties.bucketName())
                    .build();
            s3Client.headBucket(headBucketRequest);
            log.info("Bucket {} exists.", s3Properties.bucketName());
        } catch (NoSuchBucketException e) {
            log.info("Bucket {} does not exist. Creating it now.", s3Properties.bucketName());
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(s3Properties.bucketName())
                    .build();
            s3Client.createBucket(createBucketRequest);
            log.info("Bucket {} created successfully.", s3Properties.bucketName());
        } catch (S3Exception e) {
            log.error("Error checking or creating bucket: {}", e.awsErrorDetails().errorMessage());
            return "Error checking or creating bucket: " + e.awsErrorDetails().errorMessage();
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.bucketName())
                    .key(fileName)
                    .build();
            // Using RequestBody.fromInputStream for direct upload from MultipartFile
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return "File uploaded: " + fileName;
        } catch (IOException e) {
            log.error("Error reading file input stream: {}", e.getMessage());
            return "Error reading file input stream: " + e.getMessage();
        } catch (S3Exception e) {
            log.error("Error uploading file to S3: {}", e.awsErrorDetails().errorMessage());
            return "Error uploading file to S3: " + e.awsErrorDetails().errorMessage();
        }
    }

    /**
     * Downloads a file from the S3 bucket.
     *
     * @param fileName The name of the file to download.
     * @return Byte array of the downloaded file.
     * @throws IOException If an I/O error occurs during download.
     */
    public byte[] downloadFile(String fileName) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Properties.bucketName())
                .key(fileName)
                .build();
        try {
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();
        } catch (NoSuchKeyException e) {
            log.error("File not found: {}. Error: {}", fileName, e.awsErrorDetails().errorMessage());
            throw new IOException("File not found: " + fileName, e);
        } catch (S3Exception e) {
            log.error("Error downloading file from S3: {}", e.awsErrorDetails().errorMessage());
            throw new IOException("Error downloading file: " + e.awsErrorDetails().errorMessage(), e);
        }
    }


    /**
     * Deletes a file from the S3 bucket.
     *
     * @param fileName The name of the file to delete.
     * @return A message indicating success or failure.
     */
    public String deleteFile(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(s3Properties.bucketName())
                .key(fileName)
                .build();
        try {
            s3Client.deleteObject(deleteObjectRequest);
            return fileName + " removed from S3 bucket";
        } catch (S3Exception e) {
            log.error("Error deleting file from S3: {}", e.awsErrorDetails().errorMessage());
            return "Error deleting file: " + e.awsErrorDetails().errorMessage();
        }
    }

    /**
     * Lists all files in the S3 bucket.
     *
     * @return A list of file names in the bucket.
     */
    public List<String> listFiles() {
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(s3Properties.bucketName())
                .build();
        try {
            ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
            return listObjectsV2Response.contents().stream()
                    .peek(s3Object -> log.info(s3Object.toString()))
                    .map(S3Object::key)
                    .collect(Collectors.toList());
        } catch (S3Exception e) {
            log.error("Error listing files from S3: {}", e.awsErrorDetails().errorMessage());
            return List.of("Error listing files: " + e.awsErrorDetails().errorMessage());
        }
    }


}

