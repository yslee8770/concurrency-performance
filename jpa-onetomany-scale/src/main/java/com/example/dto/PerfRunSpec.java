package com.example.dto;

public record PerfRunSpec(
        int childSize,
        boolean collectionLoaded,
        int flushInterval,
        int replaceSize
) {}
