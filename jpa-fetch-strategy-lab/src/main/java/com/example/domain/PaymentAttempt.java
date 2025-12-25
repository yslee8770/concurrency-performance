package com.example.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentAttempt {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Order order;

    @Column(nullable = false)
    private String status;

    private PaymentAttempt(String status) {
        this.status = status;
    }

    public static PaymentAttempt of(String status) {
        return new PaymentAttempt(status);
    }

    void attach(Order order) {
        this.order = order;
    }
}
