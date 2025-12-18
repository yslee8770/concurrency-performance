package com.example.jpa_concurrency_performance_lab.dto;

import com.example.jpa_concurrency_performance_lab.util.QueryCountUtil;

public record BenchmarkResult(
        String strategy,
        long elapsedMs,
        QueryCountUtil.Snapshot statementCount
) {}
