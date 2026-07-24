package com.shopflow.backend.repository;

import com.shopflow.backend.entity.Addresses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AddressesRepository extends JpaRepository<Addresses, UUID> {

    List<Addresses> findByUserId(UUID userId);

    @Modifying
    @Query("UPDATE Addresses a SET a.primaryAddress = false WHERE a.user.id = :userId")
    void disablePrimaryAddressesByUserId(@Param("userId") UUID id);
}
