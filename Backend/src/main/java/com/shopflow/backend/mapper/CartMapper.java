package com.shopflow.backend.mapper;

import com.shopflow.backend.dto.response.CartResponse;
import com.shopflow.backend.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {

    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(cart))")
    @Mapping(target = "totalItems", expression = "java(calculateTotalItems(cart))")
    CartResponse toResponse(Cart cart);

    default BigDecimal calculateTotalPrice(Cart cart) {
        if (cart.getItems() == null) {
            return BigDecimal.ZERO;
        }
        return cart.getItems().stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getPrice() != null)
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default Integer calculateTotalItems(Cart cart){
        if(cart.getItems() == null){
            return 0;
        }
        return cart.getItems().stream()
                .mapToInt(item -> item.getQuantity())
                .sum();
    }
}
