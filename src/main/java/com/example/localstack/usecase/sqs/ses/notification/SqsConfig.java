package com.example.localstack.usecase.sqs.ses.notification;


import com.example.localstack.config.AppProperties;
import com.example.localstack.config.AwsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SqsConfig {
    private final AppProperties appProperties;
    private final AwsProperties awsProperties;

    @Bean
    public SqsClient sqsClient() {
        if ("localstack".equalsIgnoreCase(appProperties.environment())) {
            // Configure for Localstack
            log.info("Configuring SQS client for Localstack at: {}", appProperties.localstackEndpoint());
            return SqsClient.builder()
                    .region(Region.of(awsProperties.region()))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .applyMutation(builder -> {
                        builder.endpointOverride(URI.create(appProperties.localstackEndpoint()));
                    })
                    .build();
        } else {
            // Configure for AWS
            log.info("Configuring SQS client for AWS in region: {}", awsProperties.region());
            return SqsClient.builder()
                    .region(Region.of(awsProperties.region()))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
    }

    @Bean
    public SesClient sesClient() {
        // Configure for Localstack
        log.info("Configuring SES client for Localstack at: {}", appProperties.localstackEndpoint());
        if ("localstack".equalsIgnoreCase(appProperties.environment())) {
            return SesClient.builder()
                    .region(Region.of(awsProperties.region()))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .applyMutation(builder -> {
                        builder.endpointOverride(URI.create(appProperties.localstackEndpoint()));
                    })
                    .build();

        } else {
            // Configure for AWS
            log.info("Configuring SES client for AWS in region: {}", awsProperties.region());
            return SesClient.builder()
                    .region(Region.of(awsProperties.region()))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    /*.applyMutation(builder -> {
                        builder.endpointOverride(URI.create(appProperties.localstackEndpoint()));
                    })*/
                    .build();

        }
    }

    @Bean
    public String notificationQueueUrl(SqsClient sqsClient) {
        return sqsClient.getQueueUrl(builder -> {
            builder.queueName("notification_queue_sqs");
        }).queueUrl();
    }
}