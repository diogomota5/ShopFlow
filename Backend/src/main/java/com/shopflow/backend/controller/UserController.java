package com.shopflow.backend.controller;

import com.shopflow.backend.dto.request.UserUpdateRequest;
import com.shopflow.backend.dto.response.ApiResponse;
import com.shopflow.backend.dto.response.UserResponse;
import com.shopflow.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Usuário atualizado com sucesso", response, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> findAll() {
        List<UserResponse> response = userService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Usuários recuperados com sucesso", response, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable UUID id) {
        UserResponse response = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Usuário recuperado com sucesso", response, HttpStatus.OK));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo() {
        UserResponse response = userService.getUserInfo();
        return ResponseEntity.ok(
                ApiResponse.success("Informações do usuário recuperadas", response, HttpStatus.OK));
    }
}
