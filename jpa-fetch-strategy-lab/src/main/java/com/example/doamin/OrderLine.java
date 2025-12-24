package com.example.doamin;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderLine {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Order order;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @OrderColumn(name = "opt_seq")
    @OneToMany(mappedBy = "orderLine", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<LineOption> options = new ArrayList<>();

    private OrderLine(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }

    public static OrderLine of(String productName, int quantity) {
        return new OrderLine(productName, quantity);
    }

    void attach(Order order) {
        this.order = order;
    }

    public void addOption(LineOption option) {
        option.attach(this);
        this.options.add(option);
    }
}