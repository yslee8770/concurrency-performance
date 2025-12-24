package com.example.repository;

import com.example.doamin.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderQueryRepository {

    private final JPAQueryFactory query;

    public OrderQueryRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    // 1) 페이지 대상 ID만 가져오기
    public List<Long> findOrderIds(int offset, int limit) {
        QOrder o = QOrder.order;
        return query
                .select(o.id)
                .from(o)
                .orderBy(o.id.asc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    // 2) to-one만 fetch join
    public List<Order> fetchToOneByIds(List<Long> ids) {
        QOrder o = QOrder.order;
        QMember m = QMember.member;
        QDelivery d = QDelivery.delivery;

        return query
                .selectFrom(o)
                .join(o.member, m).fetchJoin()
                .join(o.delivery, d).fetchJoin()
                .where(o.id.in(ids))
                .orderBy(o.id.asc())
                .fetch();
    }

    // 3) 컬렉션 1개 fetch join (중간 위험)
    public List<Order> fetchLinesByIds(List<Long> ids) {
        QOrder o = QOrder.order;
        QMember m = QMember.member;
        QDelivery d = QDelivery.delivery;
        QOrderLine l = QOrderLine.orderLine;

        return query
                .selectFrom(o)
                .join(o.member, m).fetchJoin()
                .join(o.delivery, d).fetchJoin()
                .leftJoin(o.orderLines, l).fetchJoin()
                .where(o.id.in(ids))
                .orderBy(o.id.asc())
                .fetch();
    }

    // 4) 컬렉션 2개 fetch join
    public List<Order> fetchLinesAndPaymentsByIds(List<Long> ids) {
        QOrder o = QOrder.order;
        QMember m = QMember.member;
        QDelivery d = QDelivery.delivery;
        QOrderLine l = QOrderLine.orderLine;
        QPaymentAttempt p = QPaymentAttempt.paymentAttempt;

        return query
                .selectFrom(o)
                .join(o.member, m).fetchJoin()
                .join(o.delivery, d).fetchJoin()
                .leftJoin(o.orderLines, l).fetchJoin()
                .leftJoin(o.payments, p).fetchJoin()
                .where(o.id.in(ids))
                .orderBy(o.id.asc())
                .fetch();
    }

    // 5) depth까지 fetch join (OOM 위험 시나리오용)
    public List<Order> fetchDeepGraphByIds(List<Long> ids) {
        QOrder o = QOrder.order;
        QMember m = QMember.member;
        QDelivery d = QDelivery.delivery;
        QOrderLine l = QOrderLine.orderLine;
        QLineOption opt = QLineOption.lineOption;
        QPaymentAttempt p = QPaymentAttempt.paymentAttempt;

        return query
                .selectFrom(o)
                .join(o.member, m).fetchJoin()
                .join(o.delivery, d).fetchJoin()
                .leftJoin(o.orderLines, l).fetchJoin()
                .leftJoin(l.options, opt).fetchJoin()
                .leftJoin(o.payments, p).fetchJoin()
                .where(o.id.in(ids))
                .orderBy(o.id.asc())
                .fetch();
    }

    public List<Long> findOrderIds(OrderSearchCond cond, int offset, int limit) {
        QOrder o = QOrder.order;
        QMember m = QMember.member;
        QPaymentAttempt p = QPaymentAttempt.paymentAttempt;

        var where = new com.querydsl.core.BooleanBuilder();
        if (cond != null) {
            if (cond.memberNamePrefix() != null && !cond.memberNamePrefix().isBlank()) {
                where.and(m.name.startsWith(cond.memberNamePrefix()));
            }
            if (cond.paymentStatusEq() != null && !cond.paymentStatusEq().isBlank()) {
                where.and(p.status.eq(cond.paymentStatusEq()));
            }
        }

        return query
                .select(o.id)
                .from(o)
                .join(o.member, m)
                .leftJoin(o.payments, p)
                .where(where)
                .distinct()
                .orderBy(o.id.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }
}
