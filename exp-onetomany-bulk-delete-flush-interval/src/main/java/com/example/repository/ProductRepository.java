package com.example.repository;

import com.example.domain.Product;
import com.example.domain.ProductStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {

    // Dirty Checking: id range를 페이지 단위로 읽기 (정렬 고정)
    @Query("""
        select p from Product p
         where p.id between :from and :to
         order by p.id asc
    """)
    List<Product> findRange(long from, long to, Pageable pageable);

    // JPQL bulk: 동일 결과(전부 newPrice로 세팅)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
        update Product p
           set p.price = :newPrice
         where p.id between :from and :to
    """)
    int bulkSetPrice(long from, long to, int newPrice);

    // 매 실행 전 초기화(공정 비교)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
        update Product p
           set p.price = :resetPrice
         where p.id between :from and :to
    """)
    int resetPrice(long from, long to, int resetPrice);


    @Modifying(clearAutomatically = false, flushAutomatically = false)
    @Query("""
        update Product p
           set p.price = :newPrice
         where p.status = :status
    """)
    int bulkUpdatePriceByStatus(int newPrice, ProductStatus status);

}