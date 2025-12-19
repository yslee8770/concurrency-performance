package com.example.jpa_concurrency_performance_lab.service.product.stragtegy;

import com.example.jpa_concurrency_performance_lab.dto.UpdateRange;

public interface BulkUpdateStrategy {
    String name();
    void execute(UpdateRange range);
}
