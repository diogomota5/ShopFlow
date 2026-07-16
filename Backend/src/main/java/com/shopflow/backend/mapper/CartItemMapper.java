package com.shopflow.backend.mapper;

import com.shopflow.backend.dto.response.CartItemResponse;
import com.shopflow.backend.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", source = "product.imageUrl")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "originalPrice", source = "product.originalPrice")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(cartItem))")
    CartItemResponse toResponse(CartItem cartItem);

    default BigDecimal calculateSubtotal(CartItem cartItem){
        if(cartItem.getProduct() == null || cartItem.getProduct().getPrice() == null){
            return BigDecimal.ZERO;
        }
        return cartItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }
}
