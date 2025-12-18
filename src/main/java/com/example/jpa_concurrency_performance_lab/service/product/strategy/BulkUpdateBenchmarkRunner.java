package com.example.jpa_concurrency_performance_lab.service.product.strategy;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.example.jpa_concurrency_performance_lab.dto.BenchmarkResult;
import com.example.jpa_concurrency_performance_lab.dto.UpdateRange;
import com.example.jpa_concurrency_performance_lab.repository.ProductRepository;
import com.example.jpa_concurrency_performance_lab.util.QueryCountUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class BulkUpdateBenchmarkRunner {

    private final ProductRepository repository;
    private final List<BulkUpdateStrategy> strategies;

    public List<BenchmarkResult> run(UpdateRange range, int resetPrice) {
        List<BenchmarkResult> results = new ArrayList<>();

        // 실행 순서 고정(매번 같은 순서로 돌려서 비교 흔들림 줄이기)
        List<BulkUpdateStrategy> ordered = strategies.stream()
                .sorted(Comparator.comparing(BulkUpdateStrategy::name))
                .toList();

        // 워밍업 1회(첫 실행 JIT/초기화 비용 제거)
        warmUp(range, resetPrice, ordered);

        for (BulkUpdateStrategy strategy : ordered) {
            repository.resetPrice(range.fromId(), range.toId(), resetPrice);

            QueryCountUtil.reset();
            long start = System.nanoTime();

            strategy.execute(range);

            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            results.add(new BenchmarkResult(strategy.name(), elapsedMs, QueryCountUtil.snapshot()));
        }

        return results;
    }

    private void warmUp(UpdateRange range, int resetPrice, List<BulkUpdateStrategy> ordered) {
        BulkUpdateStrategy first = ordered.get(0);
        repository.resetPrice(range.fromId(), range.toId(), resetPrice);
        QueryCountUtil.reset();
        first.execute(range);
    }
}
