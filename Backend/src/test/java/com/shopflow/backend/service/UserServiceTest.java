package com.shopflow.backend.service;

import com.shopflow.backend.config.JWTUserData;
import com.shopflow.backend.dto.request.UserUpdateRequest;
import com.shopflow.backend.dto.response.UserResponse;
import com.shopflow.backend.entity.User;
import com.shopflow.backend.entity.enums.Role;
import com.shopflow.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateUserAllowsUserToEditOwnProfileWhenAuthenticatedWithJwtPrincipal() {
        UUID userId = UUID.randomUUID();
        User currentUser = User.builder()
                .id(userId)
                .name("João")
                .email("joao@email.com")
                .password("encoded")
                .role(Role.USER)
                .telephone("123456789")
                .active(true)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                JWTUserData.builder()
                        .userId(userId)
                        .email("joao@email.com")
                        .Role("USER")
                        .build(),
                null,
                List.of()
        ));

        UserResponse response = userService.updateUser(userId, new UserUpdateRequest(
                "João Atualizado",
                "joao.novo@email.com",
                "987654321",
                null,
                null
        ));

        assertEquals("João Atualizado", response.name());
        assertEquals("joao.novo@email.com", response.email());
        assertEquals("987654321", response.telephone());
        verify(userRepository).save(any(User.class));
    }
}
