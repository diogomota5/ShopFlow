package com.shopflow.backend.service;

import com.shopflow.backend.config.JWTUserData;
import com.shopflow.backend.dto.request.UserUpdateRequest;
import com.shopflow.backend.dto.response.UserResponse;
import com.shopflow.backend.entity.User;
import com.shopflow.backend.entity.enums.Role;
import com.shopflow.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        User currentUser = getAuthenticatedUser();

        if (currentUser.getRole() != Role.ADMIN && !currentUser.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Você não tem permissão para alterar os dados de outro usuário.");
        }

        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        userToUpdate.setName(request.name());
        userToUpdate.setEmail(request.email());
        userToUpdate.setTelephone(request.telephone());

        if (currentUser.getRole() == Role.ADMIN) {
            if (request.role() != null) {
                userToUpdate.setRole(Role.valueOf(request.role().toUpperCase()));
            }
            if (request.active() != null) {
                userToUpdate.setActive(request.active());
            }
        }

        userRepository.save(userToUpdate);

        return new UserResponse(
                userToUpdate.getId(),
                userToUpdate.getName(),
                userToUpdate.getEmail(),
                userToUpdate.getTelephone(),
                userToUpdate.getRole().name(),
                userToUpdate.isActive(),
                userToUpdate.getCreated_at(),
                userToUpdate.getUpdated_at());
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponse(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getTelephone(),
                        u.getRole().name(),
                        u.isActive(),
                        u.getCreated_at(),
                        u.getUpdated_at()))
                .toList();
    }

    public UserResponse findById(UUID id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        return new UserResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getTelephone(),
                u.getRole().name(),
                u.isActive(),
                u.getCreated_at(),
                u.getUpdated_at());
    }

    public UserResponse getUserInfo() {
        User currentUser = getAuthenticatedUser();
        return new UserResponse(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getEmail(),
                currentUser.getTelephone(),
                currentUser.getRole().name(),
                currentUser.isActive(),
                currentUser.getCreated_at(),
                currentUser.getUpdated_at());
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof JWTUserData userData)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }

        return userRepository.findById(userData.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado."));
    }
}
