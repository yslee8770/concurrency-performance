package com.example.jpa_concurrency_performance_lab.dto;

public record UpdateRange(long fromId, long toId, int newPrice) {}