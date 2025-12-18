package com.example.jpa_concurrency_performance_lab.domain.order;

import com.example.jpa_concurrency_performance_lab.domain.BaseEntity;
import com.example.jpa_concurrency_performance_lab.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_orders_member_id", columnList = "member_id"),
                @Index(name = "idx_orders_status", columnList = "status")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false)
    private int totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLines = new ArrayList<>();

    @Builder
    private Order(Member member, OrderStatus status, int totalAmount) {
        this.member = member;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public static Order create(Member member) {
        return Order.builder()
                .member(member)
                .status(OrderStatus.CREATED)
                .totalAmount(0)
                .build();
    }

    public void addLine(OrderLine line) {
        this.orderLines.add(line);
        line.attachOrder(this);
        recalcTotalAmount();
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    public void markPaid() {
        this.status = OrderStatus.PAID;
    }

    public void recalcTotalAmount() {
        this.totalAmount = this.orderLines.stream()
                .mapToInt(OrderLine::lineAmount)
                .sum();
    }
}