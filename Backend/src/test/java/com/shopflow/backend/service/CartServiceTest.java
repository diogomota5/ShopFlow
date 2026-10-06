package com.shopflow.backend.service;

import com.shopflow.backend.config.JWTUserData;
import com.shopflow.backend.dto.request.CartItemRequest;
import com.shopflow.backend.dto.request.CartItemUpdateRequest;
import com.shopflow.backend.dto.response.CartResponse;
import com.shopflow.backend.entity.Cart;
import com.shopflow.backend.entity.CartItem;
import com.shopflow.backend.entity.Product;
import com.shopflow.backend.entity.User;
import com.shopflow.backend.exception.BusinessRuleException;
import com.shopflow.backend.mapper.CartMapper;
import com.shopflow.backend.repository.CartItemRepository;
import com.shopflow.backend.repository.CartRepository;
import com.shopflow.backend.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    private UUID userId;
    private User user;
    private Cart cart;
    private Product product;
    private CartResponse mockCartResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder().id(userId).email("test@email.com").build();
        cart = Cart.builder().id(UUID.randomUUID()).user(user).items(new ArrayList<>()).build();
        product = Product.builder().id(UUID.randomUUID()).name("Test Prod").active(true).stock(10).build();

        mockCartResponse = new CartResponse(cart.getId(), new ArrayList<>(), java.math.BigDecimal.ZERO, 0);

        JWTUserData jwtUserData = new JWTUserData(userId, "test@email.com", "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(jwtUserData, null, new ArrayList<>()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addItem_Success() {
        CartItemRequest request = new CartItemRequest(product.getId(), 2);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toResponse(any(Cart.class))).thenReturn(mockCartResponse);

        CartResponse response = cartService.addItem(request);

        assertNotNull(response);
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test
    void addItem_ProductNotActive() {
        product.setActive(false);
        CartItemRequest request = new CartItemRequest(product.getId(), 2);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(BusinessRuleException.class, () -> cartService.addItem(request));
    }

    @Test
    void addItem_InsufficientStock() {
        CartItemRequest request = new CartItemRequest(product.getId(), 15);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(BusinessRuleException.class, () -> cartService.addItem(request));
    }
}
