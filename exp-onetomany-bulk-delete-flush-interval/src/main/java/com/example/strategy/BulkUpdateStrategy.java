package com.example.strategy;


import com.example.dto.UpdateRange;

public interface BulkUpdateStrategy {
    String name();
    void execute(UpdateRange range);
}
