package com.example.strategy;

import com.example.dto.UpdateRange;
import com.example.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaBulkUpdateStrategy implements BulkUpdateStrategy {

    private final ProductRepository repository;

    @Override
    public String name() {
        return "JPQL_BULK";
    }

    @Override
    @Transactional
    public void execute(UpdateRange range) {
        repository.bulkSetPrice(range.fromId(), range.toId(), range.newPrice());
    }
}
