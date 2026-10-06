package com.shopflow.backend.mapper;

import com.shopflow.backend.dto.response.OrderItemResponse;
import com.shopflow.backend.dto.response.OrderResponse;
import com.shopflow.backend.entity.Order;
import com.shopflow.backend.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "statusHistory", ignore = true)
    OrderResponse toResponse(Order order);

    List<OrderResponse> toResponseList(List<Order> orders);

    @Mapping(source = "product.id", target = "productId")
    OrderItemResponse toItemResponse(OrderItem item);
}
