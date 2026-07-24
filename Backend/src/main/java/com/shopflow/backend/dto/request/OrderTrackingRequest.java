package com.shopflow.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OrderTrackingRequest(
        @NotBlank(message = "O código de rastreio é obrigatório para despachar o pedido")
        String trackingCode){}
