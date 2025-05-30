package com.example.localstack;

import org.springframework.boot.SpringApplication;

public class TestSpringBootAwsLocalstackApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringBootAwsLocalstackApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
