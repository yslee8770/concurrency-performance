package com.example.service;

import com.example.domain.ProductStatus;
import com.example.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductBulkUpdateService {

    private final ProductRepository productRepository;
    private final EntityManager em;

    @Transactional
    public void bulkUpdatePrice(ProductStatus status, int newPrice) {
        productRepository.bulkUpdatePriceByStatus(newPrice, status);
    }

    @Transactional
    public void bulkUpdatePriceAndClear(ProductStatus status, int newStatus) {
        productRepository.bulkUpdatePriceByStatus(newStatus, status);
        em.clear();
    }
}
