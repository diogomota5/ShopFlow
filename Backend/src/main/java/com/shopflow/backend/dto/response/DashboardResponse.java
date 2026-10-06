package com.shopflow.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        long productCount,
        long categoryCount,
        long orderCount,
        long pendingOrderCount,
        BigDecimal revenue,
        List<DashboardTopProduct> topProducts,
        List<DashboardDailySales> salesByDay) {
    public record DashboardTopProduct(String name, long quantity) {}
    public record DashboardDailySales(String date, BigDecimal total) {}
}
