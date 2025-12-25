package com.example.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    private Delivery delivery;

    @OrderColumn(name = "line_seq")
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrderLine> orderLines = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<PaymentAttempt> payments = new LinkedHashSet<>();

    private Order(Member member, Delivery delivery) {
        this.member = member;
        this.delivery = delivery;
    }

    public static Order create(Member member, Delivery delivery) {
        return new Order(member, delivery);
    }

    public void addLine(OrderLine line) {
        line.attach(this);
        this.orderLines.add(line);
    }

    public void addPayment(PaymentAttempt payment) {
        payment.attach(this);
        this.payments.add(payment);
    }
}
