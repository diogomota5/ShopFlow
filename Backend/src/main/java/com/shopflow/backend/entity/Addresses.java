package com.shopflow.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Addresses {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "zip_code", nullable = false, length = 9)
    private String zipCode;

    @Column(name = "street", nullable = false, length = 200)
    private String street;

    @Column(name = "street_number", nullable = false, length = 10)
    private String streetNumber;

    @Column(name = "complement", length = 100)
    private String complement;

    @Column(name = "neighborhood", nullable = false, length = 100)
    private String neighborhood;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 2)
    private String state;

    @Column(name = "primary_address", nullable = false)
    private Boolean primaryAddress = true;
}
