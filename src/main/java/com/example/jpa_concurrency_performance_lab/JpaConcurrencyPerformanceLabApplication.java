package com.example.jpa_concurrency_performance_lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class JpaConcurrencyPerformanceLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpaConcurrencyPerformanceLabApplication.class, args);
	}

}
