package com.shopflow.backend.specification;

import com.shopflow.backend.entity.Product;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ProductSpecification {

    public static Specification<Product> withFilters(
            String name,
            UUID categoryId,
            BigDecimal precoMin,
            BigDecimal precoMax,
            Boolean active) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(query != null){
                query.distinct(true);

                if(query.getResultType() != Long.class){
                    root.fetch("category", JoinType.LEFT);
                }
            }

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + name.toLowerCase().trim() + "%"
                ));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (precoMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), precoMin));
            }
            if (precoMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), precoMax));
            }
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
