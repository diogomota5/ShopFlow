package com.shopflow.backend.mapper;

import com.shopflow.backend.dto.response.CartResponse;
import com.shopflow.backend.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {

    CartResponse toResponse(Cart cart);
}
