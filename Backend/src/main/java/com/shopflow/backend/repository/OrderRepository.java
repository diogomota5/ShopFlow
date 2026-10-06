package com.shopflow.backend.repository;

import com.shopflow.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.shopflow.backend.entity.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    interface TopProductProjection { String getName(); Long getQuantity(); }
    interface DailySalesProjection { LocalDate getDate(); BigDecimal getTotal(); }

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") UUID id);

    @Query("select count(o) from Order o where o.createdAt >= coalesce(:start, o.createdAt) and o.createdAt <= coalesce(:end, o.createdAt)")
    long countForDashboard(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);

    @Query("select count(o) from Order o where o.status = :status and o.createdAt >= coalesce(:start, o.createdAt) and o.createdAt <= coalesce(:end, o.createdAt)")
    long countForDashboardByStatus(@Param("status") OrderStatus status, @Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);

    @Query("select coalesce(sum(o.total), 0) from Order o where o.status <> com.shopflow.backend.entity.enums.OrderStatus.CANCELED and o.createdAt >= coalesce(:start, o.createdAt) and o.createdAt <= coalesce(:end, o.createdAt)")
    BigDecimal sumCompletedRevenue(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);

    @Query("select oi.productName as name, sum(oi.quantity) as quantity from OrderItem oi where oi.order.status <> com.shopflow.backend.entity.enums.OrderStatus.CANCELED and oi.order.createdAt >= coalesce(:start, oi.order.createdAt) and oi.order.createdAt <= coalesce(:end, oi.order.createdAt) group by oi.productName order by sum(oi.quantity) desc")
    List<TopProductProjection> findTopProducts(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end, Pageable pageable);

    @Query("select function('date', o.createdAt) as date, sum(o.total) as total from Order o where o.status <> com.shopflow.backend.entity.enums.OrderStatus.CANCELED and o.createdAt >= coalesce(:start, o.createdAt) and o.createdAt <= coalesce(:end, o.createdAt) group by function('date', o.createdAt) order by function('date', o.createdAt)")
    List<DailySalesProjection> findDailySales(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);
}
