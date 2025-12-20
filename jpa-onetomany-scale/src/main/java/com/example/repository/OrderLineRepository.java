package com.example.repository;

import com.example.doamin.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {

    long countByOrder_Id(Long orderId);

    long deleteByOrder_Id(Long orderId);
}
