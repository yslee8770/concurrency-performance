package com.example.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "product",
        indexes = {
                @Index(name = "idx_product_price", columnList = "price"),
                @Index(name = "idx_product_status", columnList = "status"),
                @Index(name = "idx_product_category", columnList = "category")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false, length = 60)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Stock stock;

    @Builder
    private Product(String name, int price, String category, ProductStatus status) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.status = status;
    }

    public static Product create(String name, int price, String category, int initialQty) {
        Product product = builder()
                .name(name)
                .price(price)
                .category(category)
                .status(ProductStatus.ON)
                .build();

        Stock stock = Stock.create(product, initialQty);
        product.attachStock(stock);

        return product;
    }

    public void attachStock(Stock stock) {
        this.stock = stock;
        if (stock != null && stock.getProduct() != this) {
            stock.attachProduct(this);
        }
    }

    public void changePrice(int price) {
        this.price = price;
    }

    public void rename(String name) {
        this.name = name;
    }

    public void turnOn() {
        this.status = ProductStatus.ON;
    }

    public void turnOff() {
        this.status = ProductStatus.OFF;
    }
}
