package com.example.localstack.usecase.s3.crud;


import com.example.localstack.config.AppProperties;
import com.example.localstack.config.AwsProperties;
import com.example.localstack.config.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Configuration class for setting up the AmazonS3 client.
 * This class conditionally configures the S3 client to connect to either
 * Localstack or actual AWS S3 based on the 'app.s3.environment' property.
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class S3Config {

    private final AppProperties appProperties;
    private final S3Properties s3Properties;
    private final AwsProperties awsProperties;


    /**
     * Creates and configures the S3Client bean.
     * The client is configured to connect to Localstack if `app.s3.environment` is "localstack",
     * otherwise it connects to AWS S3.
     *
     * @return Configured S3Client.
     */
    @Bean
    public S3Client s3Client() {
        if ("localstack".equalsIgnoreCase(appProperties.environment())) {
            // Configure for Localstack
            log.info("Configuring S3 client for Localstack at: {}", appProperties.localstackEndpoint());
            return S3Client.builder()
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .endpointOverride(URI.create(appProperties.localstackEndpoint()))
                    .region(Region.of(s3Properties.signingRegion()))
                    .forcePathStyle(s3Properties.pathStyleAccess()) // Required for Localstack S3
                    .build();
        } else {
            // Configure for AWS
            log.info("Configuring S3 client for AWS in region: {}", awsProperties.region());
            return S3Client.builder()
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .region(Region.of(awsProperties.region()))
                    .build();
        }
    }
}
