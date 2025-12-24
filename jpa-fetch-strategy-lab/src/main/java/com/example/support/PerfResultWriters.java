package com.example.support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class PerfResultWriters {

    private PerfResultWriters() {}

    public static void writeCsv(Path file, List<PerfResult> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("label,elapsedMs,heapDeltaBytes,queryCount,entityLoadCount,collectionFetchCount\n");
        for (PerfResult r : rows) {
            sb.append(escape(r.label())).append(',')
                    .append(r.elapsedMs()).append(',')
                    .append(r.heapDeltaBytes()).append(',')
                    .append(r.queryCount()).append(',')
                    .append(r.entityLoadCount()).append(',')
                    .append(r.collectionFetchCount()).append('\n');
        }
        write(file, sb.toString());
    }

    public static void writeMarkdownTable(Path file, List<PerfResult> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("| Label | Time(ms) | Heap Δ(bytes) | Queries | Entity Load | Collection Fetch |\n");
        sb.append("|---|---:|---:|---:|---:|---:|\n");
        for (PerfResult r : rows) {
            sb.append("| ").append(r.label()).append(" | ")
                    .append(r.elapsedMs()).append(" | ")
                    .append(r.heapDeltaBytes()).append(" | ")
                    .append(r.queryCount()).append(" | ")
                    .append(r.entityLoadCount()).append(" | ")
                    .append(r.collectionFetchCount()).append(" |\n");
        }
        write(file, sb.toString());
    }

    private static void write(Path file, String content) {
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write perf result file: " + file, e);
        }
    }

    private static String escape(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}