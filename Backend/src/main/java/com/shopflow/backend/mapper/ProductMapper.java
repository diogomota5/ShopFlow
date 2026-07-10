package com.shopflow.backend.mapper;

import com.shopflow.backend.dto.response.ProductResponse;
import com.shopflow.backend.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.id", target = "categoryId")
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);
}