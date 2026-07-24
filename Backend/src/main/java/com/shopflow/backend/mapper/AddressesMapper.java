package com.shopflow.backend.mapper;

import com.shopflow.backend.dto.request.AddressesRequest;
import com.shopflow.backend.dto.response.AddressesResponse;
import com.shopflow.backend.entity.Addresses;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressesMapper {

    AddressesResponse toResponse(Addresses addresses);

    List<AddressesResponse> toResponseList(List<Addresses> addressesList);

    Addresses toEntity(AddressesRequest request);

    void updateEntityFromRequest(AddressesRequest request, @MappingTarget Addresses addresses);
}
