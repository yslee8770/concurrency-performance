package com.example.support;

public record PerfResult(
        String label,
        long elapsedMs,
        long heapDeltaBytes,
        long queryCount,
        long entityLoadCount,
        long collectionFetchCount
) {}
