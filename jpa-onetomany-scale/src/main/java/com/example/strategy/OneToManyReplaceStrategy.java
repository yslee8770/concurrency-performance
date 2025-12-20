package com.example.strategy;

import com.example.dto.PerfRunSpec;

public interface OneToManyReplaceStrategy {
    String name();
    void replaceAll(Long orderId, PerfRunSpec spec);
}
