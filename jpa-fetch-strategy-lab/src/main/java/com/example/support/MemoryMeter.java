package com.example.support;

public final class MemoryMeter {

    private MemoryMeter() {}

    public static long usedBytes() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }
}
