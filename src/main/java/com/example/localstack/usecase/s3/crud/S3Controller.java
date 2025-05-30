package com.example.localstack.usecase.s3.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST Controller for handling S3 file operations.
 */
@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    /**
     * Endpoint to upload a file to S3.
     *
     * @param file The file to upload.
     * @return A message indicating the upload status.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file) {
        String result = s3Service.uploadFile(file);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Endpoint to download a file from S3.
     *
     * @param fileName The name of the file to download.
     * @return The file as a ByteArrayResource.
     * @throws IOException If an I/O error occurs.
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) throws IOException {
        byte[] data = s3Service.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    /**
     * Endpoint to delete a file from S3.
     *
     * @param fileName The name of the file to delete.
     * @return A message indicating the deletion status.
     */
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        String result = s3Service.deleteFile(fileName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Endpoint to list all files in the S3 bucket.
     *
     * @return A list of file names.
     */
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {
        List<String> files = s3Service.listFiles();
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
}
