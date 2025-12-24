package com.example.fixture;

import com.example.doamin.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestDataFactory {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public List<Long> seed(
            int orders,
            int linesPerOrder,
            int optionsPerLine,
            int paymentsPerOrder
    ) {
        List<Long> orderIds = new ArrayList<>(orders);

        for (int i = 1; i <= orders; i++) {
            Member member = Member.of("m-" + i);
            em.persist(member);

            Delivery delivery = Delivery.of("addr-" + i);

            Order order = Order.create(member, delivery);

            for (int l = 1; l <= linesPerOrder; l++) {
                OrderLine line = OrderLine.of("p-" + l, 1);

                for (int o = 1; o <= optionsPerLine; o++) {
                    line.addOption(LineOption.of("opt-" + o));
                }
                order.addLine(line);
            }

            for (int p = 1; p <= paymentsPerOrder; p++) {
                order.addPayment(PaymentAttempt.of("TRY_" + p));
            }

            em.persist(order);
            orderIds.add(order.getId());

            if (i % 200 == 0) {
                em.flush();
                em.clear();
            }
        }

        em.flush();
        em.clear();
        return orderIds;
    }
}
