package com.example.localstack.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app") // Binds properties with prefix "app"
public record AppProperties(String environment, String localstackEndpoint) {
}