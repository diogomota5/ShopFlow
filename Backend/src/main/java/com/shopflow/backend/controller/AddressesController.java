package com.shopflow.backend.controller;

import com.shopflow.backend.dto.request.AddressesRequest;
import com.shopflow.backend.dto.response.AddressesResponse;
import com.shopflow.backend.dto.response.ApiResponse;
import com.shopflow.backend.service.AddressesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/adresses")
@RequiredArgsConstructor
public class AddressesController {

    private final AddressesService addressesService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AddressesResponse>> createAddress(@Valid @RequestBody AddressesRequest request) {
        AddressesResponse address = addressesService.createAddress(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Endereço cadastrado com sucesso", address, HttpStatus.CREATED));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AddressesResponse>>> getMyAddresses() {
        List<AddressesResponse> addresses = addressesService.getMyAddresses();
        return ResponseEntity.ok(
                ApiResponse.success("Endereços recuperados com sucesso", addresses, HttpStatus.OK));
    }

    @PutMapping("update/{addressId}")
    public ResponseEntity<ApiResponse<AddressesResponse>> updateAddress(@PathVariable UUID addressId,
            @Valid @RequestBody AddressesRequest request) {
        AddressesResponse address = addressesService.updateAddress(addressId, request);
        return ResponseEntity.ok(
                ApiResponse.success("Endereço atualizado com sucesso", address, HttpStatus.OK));
    }

    @DeleteMapping("/delete/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable UUID addressId) {
        addressesService.deleteAddress(addressId);
        return ResponseEntity.ok(
                ApiResponse.success("Endereço removido com sucesso", null, HttpStatus.OK));
    }

    // --- ADMIN ENDPOINTS ---

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/user/{userId}/create")
    public ResponseEntity<ApiResponse<AddressesResponse>> createAddressForUser(@PathVariable UUID userId,
            @Valid @RequestBody AddressesRequest request) {
        AddressesResponse address = addressesService.createAddressForUser(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Endereço cadastrado com sucesso pelo Admin", address, HttpStatus.CREATED));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AddressesResponse>>> getAddressesByUser(@PathVariable UUID userId) {
        List<AddressesResponse> addresses = addressesService.getAddressesByUserId(userId);
        return ResponseEntity.ok(
                ApiResponse.success("Endereços recuperados com sucesso", addresses, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("update/admin/{addressId}")
    public ResponseEntity<ApiResponse<AddressesResponse>> updateAddressAsAdmin(@PathVariable UUID addressId,
            @Valid @RequestBody AddressesRequest request) {
        AddressesResponse address = addressesService.updateAddressAsAdmin(addressId, request);
        return ResponseEntity.ok(
                ApiResponse.success("Endereço atualizado com sucesso pelo Admin", address, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/admin/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddressAsAdmin(@PathVariable UUID addressId) {
        addressesService.deleteAddressAsAdmin(addressId);
        return ResponseEntity.ok(
                ApiResponse.success("Endereço removido com sucesso pelo Admin", null, HttpStatus.OK));
    }
}