package com.shopflow.backend.mapper;

import com.shopflow.backend.dto.request.AssessmentRequest;
import com.shopflow.backend.dto.response.AssessmentResponse;
import com.shopflow.backend.entity.Assessment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssessmentMapper {

    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "product.id", target = "productId")
    AssessmentResponse toResponse(Assessment assessment);

    List<AssessmentResponse> toResponseList(List<Assessment> assessments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Assessment toEntity(AssessmentRequest request);
}
