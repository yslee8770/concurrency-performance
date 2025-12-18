package com.example.jpa_concurrency_performance_lab.util;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;

public final class QueryCountUtil {


    private QueryCountUtil() {}

    public static void reset() {
        QueryCountHolder.clear();
    }

    public static Snapshot snapshot() {
        QueryCount c = QueryCountHolder.getGrandTotal();
        long total = c.getSelect() + c.getInsert() + c.getUpdate() + c.getDelete();
        return new Snapshot(c.getSelect(), c.getInsert(), c.getUpdate(), c.getDelete(), total);
    }

    public record Snapshot(long select, long insert, long update, long delete, long total) {}

    public static String format(Snapshot s) {
        return "SELECT=" + s.select()
                + ", INSERT=" + s.insert()
                + ", UPDATE=" + s.update()
                + ", DELETE=" + s.delete()
                + ", TOTAL=" + s.total();
    }
}
