package com.example.jpa_concurrency_performance_lab.domain.order;

import com.example.jpa_concurrency_performance_lab.domain.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(
        name = "order_line",
        indexes = {
                @Index(name = "idx_order_line_order_id", columnList = "order_id"),
                @Index(name = "idx_order_line_product_id", columnList = "product_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderLine {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_line_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int orderPrice;

    @Column(nullable = false)
    private int quantity;

    @Builder
    private OrderLine(Product product, int orderPrice, int quantity) {
        this.product = product;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
    }

    public static OrderLine create(Product product, int quantity) {
        return OrderLine.builder()
                .product(product)
                .orderPrice(product.getPrice())
                .quantity(quantity)
                .build();
    }

    void attachOrder(Order order) {
        this.order = order;
    }

    public int lineAmount() {
        return this.orderPrice * this.quantity;
    }
}

