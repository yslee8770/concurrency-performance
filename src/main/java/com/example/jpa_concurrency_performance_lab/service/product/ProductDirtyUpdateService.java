package com.example.jpa_concurrency_performance_lab.service.product;

import com.example.jpa_concurrency_performance_lab.domain.product.Product;
import com.example.jpa_concurrency_performance_lab.domain.product.ProductStatus;
import com.example.jpa_concurrency_performance_lab.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductDirtyUpdateService {

    private final ProductRepository productRepository;

    @Transactional
    public void dirtyUpdatePrice(ProductStatus status, int newPrice) {
        productRepository.findAll().stream()
                .filter(p -> p.getStatus() == status)
                .forEach(p -> p.changePrice(newPrice));
    }
}
