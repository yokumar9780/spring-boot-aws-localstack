package com.example.localstack.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "s3") // Binds properties with prefix "s3"
public record S3Properties(
        /**
         * The S3 signing region.
         */
        String signingRegion,
        /**
         * Whether to use path-style access for S3 buckets.
         */
        boolean pathStyleAccess,
        /**
         * The name of the S3 bucket.
         */
        String bucketName
) {
}
