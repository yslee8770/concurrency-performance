package com.example.doamin;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LineOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private OrderLine orderLine;

    @Column(nullable = false)
    private String name;

    private LineOption(String name) {
        this.name = name;
    }

    public static LineOption of(String name) {
        return new LineOption(name);
    }

    void attach(OrderLine line) {
        this.orderLine = line;
    }
}