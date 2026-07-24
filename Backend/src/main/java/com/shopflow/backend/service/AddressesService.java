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
    public AddressesResponse createAddress(AddressesRequest request){
        UUID userId = getLoggedUserId();

        if(request.primaryAddress() || addressesRepository.findByUserId(userId).isEmpty()){
            addressesRepository.disablePrimaryAddressesByUserId(userId);
        }

        Addresses address =addressesMapper.toEntity(request);
        address.setUser(User.builder().id(userId).build());

        if(addressesRepository.findByUserId(userId).isEmpty()){
            address.setPrimaryAddress(true);
        }

        Addresses saved = addressesRepository.save(address);
        return addressesMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AddressesResponse> getMyAddresses(){
        UUID userId = getLoggedUserId();
        List<Addresses> addresses = addressesRepository.findByUserId(userId);
        return addressesMapper.toResponseList(addresses);
    }

    @Transactional
    public AddressesResponse updateAddress(UUID addressId, AddressesRequest request){
        UUID userId = getLoggedUserId();
        Addresses address = addressesRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado: " + addressId));

        if (!address.getUser().getId().equals(userId)){
            throw new BusinessRuleException("Este endereço não pertence ao seu usuário");
        }

        if(request.primaryAddress()){
            addressesRepository.disablePrimaryAddressesByUserId(userId);
        }

        addressesMapper.updateEntityFromRequest(request, address);
        Addresses updated = addressesRepository.save(address);
        return addressesMapper.toResponse(updated);
    }

    @Transactional
    public void deleteAddress(UUID addressId){
        UUID userId = getLoggedUserId();
        Addresses address = addressesRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado: " + addressId));

        if(!address.getUser().getId().equals(userId)){
            throw new BusinessRuleException("Este endereço não pertence ao seu usuário");
        }

        if (address.getPrimaryAddress()){
            throw new BusinessRuleException("Não é possível deletar seu endereço principal. Defina outro antes");
        }

        addressesRepository.delete(address);
    }

    private UUID getLoggedUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken){
            throw new SecurityAuthenticationException("Usuário não está autenticado");
        }
        return ((JWTUserData) authentication.getPrincipal()).userId();
    }
}
