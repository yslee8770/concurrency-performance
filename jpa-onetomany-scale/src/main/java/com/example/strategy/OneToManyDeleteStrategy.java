package com.example.strategy;

import com.example.dto.PerfRunSpec;

public interface OneToManyDeleteStrategy {
    String name();

    void deleteAll(Long orderId, PerfRunSpec spec);
}