package com.shopflow.backend.repository;

import com.shopflow.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByActiveTrue();

    boolean existsByNameIgnoreCaseAndActiveTrue(String name);

    @Query("""
       SELECT c FROM Category c
       WHERE CAST(function('unaccent', lower(trim(c.name))) AS string)
       LIKE CAST(function('unaccent', lower(trim(concat('%', :name, '%')))) AS string)
       AND c.active = true 
       """)
    List<Category> findByNameForUser(@Param("name") String name);

    @Query("""
       SELECT c FROM Category c
       WHERE CAST(function('unaccent', lower(trim(c.name))) AS string)
       LIKE CAST(function('unaccent', lower(trim(concat('%', :name, '%')))) AS string)
       """)
    List<Category> findByNameForAdmin(@Param("name") String name);
}