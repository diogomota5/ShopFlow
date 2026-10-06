package com.shopflow.backend.repository;

import com.shopflow.backend.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = { "cart" })
    Optional<User> findUserByEmail(String email);

    Optional<User> findByEmailVerificationToken(String token);

    Optional<User> findByPasswordResetToken(String token);

    @Override
    @EntityGraph(attributePaths = { "cart" })
    List<User> findAll();

    @Override
    @EntityGraph(attributePaths = { "cart" })
    Optional<User> findById(UUID id);
}
