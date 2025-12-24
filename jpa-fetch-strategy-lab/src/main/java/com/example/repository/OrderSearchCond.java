package com.example.repository;

public record OrderSearchCond(
        String memberNamePrefix,
        String paymentStatusEq
) {
    public static OrderSearchCond empty() {
        return new OrderSearchCond(null, null);
    }
}
