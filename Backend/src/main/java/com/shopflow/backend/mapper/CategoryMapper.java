package com.shopflow.backend.mapper;

import com.shopflow.backend.dto.response.CategoryResponse;
import com.shopflow.backend.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);
}
