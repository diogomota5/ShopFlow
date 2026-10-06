package com.shopflow.backend.service;

import com.shopflow.backend.config.JWTUserData;
import com.shopflow.backend.dto.request.OrderTrackingRequest;
import com.shopflow.backend.dto.response.OrderResponse;
import com.shopflow.backend.entity.Order;
import com.shopflow.backend.entity.User;
import com.shopflow.backend.entity.enums.OrderStatus;
import com.shopflow.backend.exception.BusinessRuleException;
import com.shopflow.backend.mapper.OrderMapper;
import com.shopflow.backend.repository.OrderRepository;
import com.shopflow.backend.repository.OrderStatusHistoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusHistoryRepository statusHistoryRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private UUID userId;
    private Order order;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        User user = User.builder().id(userId).email("test@mail.com").build();

        order = Order.builder()
                .id(UUID.randomUUID())
                .user(user)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        JWTUserData jwtUserData = new JWTUserData(userId, "test@mail.com", "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(jwtUserData, null, new ArrayList<>()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateToPaid_Success() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(mockOrderResponse(OrderStatus.PAID));

        OrderResponse response = orderService.updateToPaid(order.getId());

        assertEquals(OrderStatus.PAID, response.status());
        verify(statusHistoryRepository, times(1)).save(any());
        verify(emailService, times(1)).sendOrderStatusEmail(any(Order.class), eq(OrderStatus.PAID));
    }

    @Test
    void updateToPaid_Failure_NotPending() {
        order.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(BusinessRuleException.class, () -> orderService.updateToPaid(order.getId()));
    }

    @Test
    void updateToShipped_Failure_NotPaid() {
        // Order is currently PENDING. Trying to mark as SHIPPED should fail.
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        OrderTrackingRequest request = new OrderTrackingRequest("TRK123");

        assertThrows(BusinessRuleException.class, () -> orderService.updateToShipped(order.getId(), request));
    }

    @Test
    void updateToShipped_Success() {
        order.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(mockOrderResponse(OrderStatus.SHIPPED));

        OrderTrackingRequest request = new OrderTrackingRequest("TRK123");
        OrderResponse response = orderService.updateToShipped(order.getId(), request);

        assertEquals(OrderStatus.SHIPPED, response.status());
        verify(statusHistoryRepository, times(1)).save(any());
    }

    private OrderResponse mockOrderResponse(OrderStatus status) {
        return new OrderResponse(order.getId(), status, null, null, null, null, null, null, null, null, null, null,
                null, null, null, new ArrayList<>(), LocalDateTime.now());
    }
}
