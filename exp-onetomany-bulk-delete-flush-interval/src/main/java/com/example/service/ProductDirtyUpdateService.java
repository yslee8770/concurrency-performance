package com.example.service;

import com.example.domain.ProductStatus;
import com.example.repository.ProductRepository;
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
