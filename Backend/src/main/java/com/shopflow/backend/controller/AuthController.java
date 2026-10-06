package com.shopflow.backend.controller;

import com.shopflow.backend.config.TokenConfig;
import com.shopflow.backend.dto.request.LoginRequest;
import com.shopflow.backend.dto.request.RegisterUserRequest;
import com.shopflow.backend.dto.request.ForgotPasswordRequest;
import com.shopflow.backend.dto.request.ResetPasswordRequest;
import com.shopflow.backend.dto.response.LoginResponse;
import com.shopflow.backend.dto.response.RegisterUserResponse;
import com.shopflow.backend.entity.User;
import com.shopflow.backend.entity.enums.Role;
import com.shopflow.backend.exception.SecurityAuthenticationException;
import com.shopflow.backend.repository.UserRepository;
import com.shopflow.backend.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;
    private final EmailService emailService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            TokenConfig tokenConfig,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Verifica se o email foi verificado antes de autenticar
            User existingUser = userRepository.findUserByEmail(request.email()).orElse(null);
            if (existingUser != null && !existingUser.isEmailVerified()) {
                throw new SecurityAuthenticationException(
                        "Confirme seu e-mail antes de entrar. Enviamos um link para sua caixa de entrada.");
            }

            UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(),
                    request.password());
            Authentication authentication = authenticationManager.authenticate(userAndPass);

            User user = (User) authentication.getPrincipal();
            String token = tokenConfig.generateToken(user);
            return ResponseEntity.ok(new LoginResponse(token));

        } catch (SecurityAuthenticationException ex) {
            throw ex; // Propaga a mensagem de e-mail não verificado
        } catch (DisabledException ex) {
            throw new SecurityAuthenticationException("Sua conta está desativada. Entre em contato com o suporte.");
        } catch (BadCredentialsException | InternalAuthenticationServiceException ex) {
            // Mensagem genérica para não revelar se o e-mail existe ou não (OWASP)
            throw new SecurityAuthenticationException("E-mail ou senha inválidos.");
        }
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        User newUser = new User();
        newUser.setName(request.name());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(Role.USER);
        newUser.setTelephone(request.telephone());
        newUser.setActive(true);
        newUser.setEmailVerified(false);
        newUser.setEmailVerificationToken(UUID.randomUUID().toString());
        newUser.setEmailVerificationExpiresAt(LocalDateTime.now().plusHours(24));
        userRepository.save(newUser);
        emailService.sendVerificationEmail(newUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterUserResponse(newUser.getName(), newUser.getEmail()));
    }

    @Transactional
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@Valid @RequestBody ForgotPasswordRequest request) {
        User user = userRepository.findUserByEmail(request.email()).orElse(null);
        if (user != null && !user.isEmailVerified()) {
            user.setEmailVerificationToken(UUID.randomUUID().toString());
            user.setEmailVerificationExpiresAt(LocalDateTime.now().plusHours(24));
            userRepository.save(user);
            emailService.sendVerificationEmail(user);
        }
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        User user = userRepository.findUserByEmail(request.email()).orElse(null);
        if (user != null && user.isActive()) {
            user.setPasswordResetToken(UUID.randomUUID().toString());
            user.setPasswordResetExpiresAt(LocalDateTime.now().plusMinutes(30));
            userRepository.save(user);
            emailService.sendPasswordResetEmail(user);
        }
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        User user = userRepository.findByPasswordResetToken(request.token())
                .orElseThrow(() -> new SecurityAuthenticationException("Token inválido ou expirado."));
        if (user.getPasswordResetExpiresAt() == null
                || user.getPasswordResetExpiresAt().isBefore(LocalDateTime.now())) {
            throw new SecurityAuthenticationException("Token inválido ou expirado.");
        }
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiresAt(null);
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new SecurityAuthenticationException("Link de validação inválido ou expirado."));
        if (user.getEmailVerificationExpiresAt() == null
                || user.getEmailVerificationExpiresAt().isBefore(LocalDateTime.now())) {
            throw new SecurityAuthenticationException("Link de validação expirado. Solicite um novo cadastro.");
        }
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpiresAt(null);
        userRepository.save(user);
        return ResponseEntity.ok("E-mail confirmado com sucesso. Você já pode entrar na sua conta.");
    }

}
