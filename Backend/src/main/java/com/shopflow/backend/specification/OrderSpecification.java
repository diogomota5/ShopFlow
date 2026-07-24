package com.shopflow.backend.specification;

import com.shopflow.backend.entity.Order;
import com.shopflow.backend.entity.enums.OrderStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class OrderSpecification {

    public static Specification<Order> withFilters(
            UUID userId,
            OrderStatus status,
            LocalDateTime start,
            LocalDateTime end,
            BigDecimal minTotal,
            BigDecimal maxTotal){

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(query != null) {
                query.distinct(true);
            }

            if(userId != null){
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }

            if(status != null){
                predicates.add(cb.equal(root.get("status"), status));
            }

            if(start != null){
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), start));
            }

            if(end != null){
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), end));
            }
            if(minTotal != null){
                predicates.add(cb.greaterThanOrEqualTo(root.get("total"), minTotal));
            }

            if(maxTotal != null){
                predicates.add(cb.lessThanOrEqualTo(root.get("total"), maxTotal));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
