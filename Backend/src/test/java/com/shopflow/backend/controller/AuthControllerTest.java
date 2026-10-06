package com.shopflow.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.backend.config.TokenConfig;
import com.shopflow.backend.dto.request.LoginRequest;
import com.shopflow.backend.dto.request.RegisterUserRequest;
import com.shopflow.backend.entity.User;
import com.shopflow.backend.entity.enums.Role;
import com.shopflow.backend.repository.UserRepository;
import com.shopflow.backend.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
// Import manually since WebMvcTest won't load the Security Config automatically
@Import(com.shopflow.backend.config.SecurityConfig.class)
public class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private UserRepository userRepository;

        @MockBean
        private PasswordEncoder passwordEncoder;

        @MockBean
        private AuthenticationManager authenticationManager;

        @MockBean
        private TokenConfig tokenConfig;

        @MockBean
        private EmailService emailService;

        @MockBean
        private com.shopflow.backend.config.SecurityFilter securityFilter;

        private User mockUser;

        @BeforeEach
        void setUp() {
                mockUser = new User();
                mockUser.setId(java.util.UUID.randomUUID());
                mockUser.setName("Test User");
                mockUser.setEmail("test@email.com");
                mockUser.setPassword("encodedPassword");
                mockUser.setRole(Role.USER);
                mockUser.setActive(true);
                mockUser.setEmailVerified(true);
        }

        @Test
        void login_Success() throws Exception {
                LoginRequest request = new LoginRequest("test@email.com", "password123");

                Mockito.when(userRepository.findUserByEmail("test@email.com")).thenReturn(Optional.of(mockUser));

                Authentication authentication = Mockito.mock(Authentication.class);
                Mockito.when(authentication.getPrincipal()).thenReturn(mockUser);
                Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(authentication);

                Mockito.when(tokenConfig.generateToken(mockUser)).thenReturn("mocked-jwt-token");

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
        }

        @Test
        void login_Failure_EmailNotVerified() throws Exception {
                mockUser.setEmailVerified(false);
                LoginRequest request = new LoginRequest("test@email.com", "password123");

                Mockito.when(userRepository.findUserByEmail("test@email.com")).thenReturn(Optional.of(mockUser));

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message")
                                                .value("Confirme seu e-mail antes de entrar. Enviamos um link para sua caixa de entrada."));
        }

        @Test
        void register_Success() throws Exception {
                RegisterUserRequest request = new RegisterUserRequest("New User", "new@email.com", "password123",
                                "99999999999");

                Mockito.when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.name").value("New User"))
                                .andExpect(jsonPath("$.email").value("new@email.com"));

                Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
                Mockito.verify(emailService, Mockito.times(1)).sendVerificationEmail(any(User.class));
        }
}
