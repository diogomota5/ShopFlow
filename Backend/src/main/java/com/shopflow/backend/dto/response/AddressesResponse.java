package com.shopflow.backend.dto.response;

import java.util.UUID;

public record AddressesResponse(
        UUID id,
        String nickname,
        String zipCode,
        String street,
        String streetNumber,
        String complement,
        String neighborhood,
        String city,
        String state,
        Boolean primaryAddress) {
}
