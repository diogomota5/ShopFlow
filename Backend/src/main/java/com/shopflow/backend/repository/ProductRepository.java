package com.shopflow.backend.repository;

import com.shopflow.backend.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    boolean existsByNameIgnoreCaseAndActiveTrue(String name);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);

    List<Product> findByStockLessThanEqualAndActiveTrue(Integer stock);

    long countByActiveTrue();

    long countByCategoryIdAndActiveTrue(UUID categoryId);

    @Modifying
    @Query("update Product p set p.stock = p.stock - :quantity where p.id = :productId and p.active = true and p.stock >= :quantity")
    int reserveStock(@Param("productId") UUID productId, @Param("quantity") int quantity);

    @Modifying
    @Query("update Product p set p.stock = p.stock + :quantity where p.id = :productId")
    int releaseStock(@Param("productId") UUID productId, @Param("quantity") int quantity);
/*
    @Query("""
            SELECT p FROM Product p
            JOIN OrderItem oi ON oi.product.id = p.id
            GROUP BY p.id
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<Product> findMostSold(Pageable pageable);

 */
}