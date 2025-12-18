package com.example.jpa_concurrency_performance_lab.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.Optional;

@TestConfiguration
@EnableJpaAuditing(dateTimeProviderRef = "fixedDateTimeProvider")
public class TestJpaAuditingConfig {

    @org.springframework.context.annotation.Bean
    public DateTimeProvider fixedDateTimeProvider() {
        // 테스트마다 완전히 동일한 값
        return () -> Optional.of(LocalDateTime.of(2025, 1, 1, 0, 0));
    }
}
