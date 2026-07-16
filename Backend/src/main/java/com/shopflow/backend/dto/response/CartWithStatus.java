package com.shopflow.backend.dto.response;

public record CartWithStatus(CartResponse response, boolean isNew) {
}
