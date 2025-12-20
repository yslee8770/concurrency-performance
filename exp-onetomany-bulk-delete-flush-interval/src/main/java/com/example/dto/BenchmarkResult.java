package com.example.dto;


import com.example.util.QueryCountUtil;

public record BenchmarkResult(
        String strategy,
        long elapsedMs,
        QueryCountUtil.Snapshot statementCount
) {}
