package com.example.localstack;

import com.example.localstack.config.AppProperties;
import com.example.localstack.config.AwsProperties;
import com.example.localstack.config.S3Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class, AwsProperties.class, S3Properties.class})
public class SpringBootAwsLocalstackApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAwsLocalstackApplication.class, args);
    }

}
