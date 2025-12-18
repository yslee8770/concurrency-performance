package com.example.jpa_concurrency_performance_lab.fixture;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductSeed {

    private final JdbcTemplate jdbcTemplate;

    public void insertProducts(int total) {
        String[] categories = {"A", "B", "C", "D", "E"};

        List<Object[]> batch = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            String category = categories[i % categories.length];
            batch.add(new Object[]{"P-" + i, 1000, category, "ON"});
        }

        jdbcTemplate.batchUpdate(
                "insert into product(name, price, category, status, created_at, updated_at) values(?, ?, ?, ?, now(), now())",
                batch
        );
    }
}
