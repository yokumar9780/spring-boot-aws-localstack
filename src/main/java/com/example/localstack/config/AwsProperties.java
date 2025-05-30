package com.example.localstack.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "aws") // Binds properties with prefix "aws"
public record AwsProperties(
        Credentials credentials,
        String region
) {
    /**
     * Nested record representing AWS credentials.
     */
    public record Credentials(String accessKey, String secretKey) {
    }
}