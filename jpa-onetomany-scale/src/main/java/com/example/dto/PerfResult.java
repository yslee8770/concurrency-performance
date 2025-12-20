package com.example.dto;


public record PerfResult(
        String strategy,
        PerfRunSpec spec,
        long elapsedMs,
        long preparedStatementCount,
        long flushCount,
        long entityDeleteCount,
        int managedEntityCountPeak,
        long heapUsedBytesPeak
) {}
