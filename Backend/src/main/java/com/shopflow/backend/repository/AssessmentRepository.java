package com.shopflow.backend.repository;

import com.shopflow.backend.dto.response.ProductRatingResponse;
import com.shopflow.backend.entity.Assessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {

    Page<Assessment> findByProductIdOrderByCreatedAtDesc(UUID productId, Pageable pageable);

    //Quantos produtos foram avaliados com status Delivered
    @Query("""
        SELECT COUNT (o) > 0 FROM Order o
        JOIN o.items oi
        WHERE o.user.id = :userId
        AND oi.product.id = :productId
        AND o.status = 'DELIVERED'
""")
    boolean hasUserPurchasedProduct(@Param("userId") UUID userId, @Param("productId") UUID productId);

    //Média de notas e quantidade de avaliações
    @Query("""
        SELECT new  com.shopflow.backend.dto.response.ProductRatingResponse(AVG(a.rating), COUNT(a))
        FROM Assessment a
        WHERE a.product.id = :productId
""")
    ProductRatingResponse getProductRatingStats(@Param("productId") UUID productId);
}
