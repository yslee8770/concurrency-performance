package com.example.support;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Component;

@Component
public class HibernateStats {

    private final Statistics statistics;

    public HibernateStats(EntityManagerFactory emf) {
        SessionFactory sf = emf.unwrap(SessionFactory.class);
        this.statistics = sf.getStatistics();
    }

    public void clear() {
        statistics.clear();
    }

    public long queryCount() {
        return statistics.getQueryExecutionCount();
    }

    public long entityLoadCount() {
        return statistics.getEntityLoadCount();
    }

    public long collectionFetchCount() {
        return statistics.getCollectionFetchCount();
    }
}
