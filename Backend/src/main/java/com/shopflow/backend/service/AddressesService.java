package com.shopflow.backend.service;

import com.shopflow.backend.config.JWTUserData;
import com.shopflow.backend.dto.request.AddressesRequest;
import com.shopflow.backend.dto.response.AddressesResponse;
import com.shopflow.backend.entity.Addresses;
import com.shopflow.backend.entity.User;
import com.shopflow.backend.exception.BusinessRuleException;
import com.shopflow.backend.exception.SecurityAuthenticationException;
import com.shopflow.backend.mapper.AddressesMapper;
import com.shopflow.backend.repository.AddressesRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressesService {

    private final AddressesRepository addressesRepository;
    private final AddressesMapper addressesMapper;

    @Transactional
    public AddressesResponse createAddress(AddressesRequest request) {
        return createAddressInternal(getLoggedUserId(), request);
    }

    @Transactional
    public AddressesResponse createAddressForUser(UUID userId, AddressesRequest request) {
        return createAddressInternal(userId, request);
    }

    private AddressesResponse createAddressInternal(UUID userId, AddressesRequest request) {
        if (request.primaryAddress() || addressesRepository.findByUserId(userId).isEmpty()) {
            addressesRepository.disablePrimaryAddressesByUserId(userId);
        }

        Addresses address = addressesMapper.toEntity(request);
        address.setUser(User.builder().id(userId).build());

        if (addressesRepository.findByUserId(userId).isEmpty()) {
            address.setPrimaryAddress(true);
        }

        Addresses saved = addressesRepository.save(address);
        return addressesMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AddressesResponse> getMyAddresses() {
        return getAddressesByUserId(getLoggedUserId());
    }

    @Transactional(readOnly = true)
    public List<AddressesResponse> getAddressesByUserId(UUID userId) {
        List<Addresses> addresses = addressesRepository.findByUserId(userId);
        return addressesMapper.toResponseList(addresses);
    }

    @Transactional
    public AddressesResponse updateAddress(UUID addressId, AddressesRequest request) {
        return updateAddressInternal(addressId, request, false);
    }

    @Transactional
    public AddressesResponse updateAddressAsAdmin(UUID addressId, AddressesRequest request) {
        return updateAddressInternal(addressId, request, true);
    }

    private AddressesResponse updateAddressInternal(UUID addressId, AddressesRequest request, boolean isAdmin) {
        Addresses address = addressesRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado: " + addressId));

        if (!isAdmin && !address.getUser().getId().equals(getLoggedUserId())) {
            throw new BusinessRuleException("Este endereço não pertence ao seu usuário");
        }

        if (request.primaryAddress()) {
            addressesRepository.disablePrimaryAddressesByUserId(address.getUser().getId());
        }

        addressesMapper.updateEntityFromRequest(request, address);
        Addresses updated = addressesRepository.save(address);
        return addressesMapper.toResponse(updated);
    }

    @Transactional
    public void deleteAddress(UUID addressId) {
        deleteAddressInternal(addressId, false);
    }

    @Transactional
    public void deleteAddressAsAdmin(UUID addressId) {
        deleteAddressInternal(addressId, true);
    }

    private void deleteAddressInternal(UUID addressId, boolean isAdmin) {
        Addresses address = addressesRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado: " + addressId));

        if (!isAdmin && !address.getUser().getId().equals(getLoggedUserId())) {
            throw new BusinessRuleException("Este endereço não pertence ao seu usuário");
        }

        if (address.getPrimaryAddress()) {
            throw new BusinessRuleException("Não é possível deletar um endereço principal. Defina outro antes");
        }

        addressesRepository.delete(address);
    }

    private UUID getLoggedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new SecurityAuthenticationException("Usuário não está autenticado");
        }
        return ((JWTUserData) authentication.getPrincipal()).userId();
    }
}
