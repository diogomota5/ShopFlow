package com.shopflow.backend.service;

import com.shopflow.backend.config.JWTUserData;
import com.shopflow.backend.dto.request.OrderRequest;
import com.shopflow.backend.dto.request.OrderTrackingRequest;
import com.shopflow.backend.dto.response.OrderResponse;
import com.shopflow.backend.entity.*;
import com.shopflow.backend.entity.enums.OrderStatus;
import com.shopflow.backend.exception.BusinessRuleException;
import com.shopflow.backend.exception.SecurityAuthenticationException;
import com.shopflow.backend.mapper.OrderMapper;
import com.shopflow.backend.repository.AddressesRepository;
import com.shopflow.backend.repository.CouponRepository;
import com.shopflow.backend.repository.OrderRepository;
import com.shopflow.backend.specification.OrderSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final AddressesRepository addressesRepository;
    private final CouponService couponService;
    private final CouponRepository couponRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(OrderRequest request){
        UUID userId = getLoggedUserId();

        Cart cart = cartService.getOrCreateCart(userId);
        if (cart.getItems().isEmpty()){
            throw new BusinessRuleException("Seu carrinho está vazio");
        }

        Addresses address = addressesRepository.findById(request.addressId())
                .orElseThrow(() -> new EntityNotFoundException("Endereço de entrega não encontrado: " + request.addressId()));

        if(!address.getUser().getId().equals(userId)){
            throw new BusinessRuleException("Este endereço não pertence ao usuário logado");
        }

        BigDecimal subtotal = cart.getTotalPrice();
        BigDecimal discount = BigDecimal.ZERO;

        if(request.couponCode() != null && !request.couponCode().isBlank()){
            Coupon coupon = couponRepository.findByCodeIgnoreCase(request.couponCode().trim())
                    .orElseThrow(() -> new EntityNotFoundException("Cupom informado não existe: " + request.couponCode()));

            if(!coupon.isValid(subtotal)) {
                throw new BusinessRuleException("Este cupom é inválido, expirou ou não atingiu o valor mínimo de compra");
            }

            discount = couponService.calculateDiscount(coupon, subtotal);

            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }

        BigDecimal total = subtotal.subtract(discount).max(BigDecimal.ZERO);

        Order order = Order.builder()
                .user(User.builder().id(userId).build())
                .addresses((address))
                .status(OrderStatus.PENDING)
                .subtotal(subtotal)
                .discount(discount)
                .total(total)
                .notes(request.notes())
                .shippingZipCode(address.getZipCode())
                .shippingStreet(address.getStreet())
                .shippingNumber(address.getStreetNumber())
                .shippingComplement(address.getComplement())
                .shippingNeighborhood(address.getNeighborhood())
                .shippingCity(address.getCity())
                .shippingState(address.getState())
                .build();

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();

                    if(product.getStock() < cartItem.getQuantity()){
                        throw new BusinessRuleException("Estoque insuficiente para o produto: " + product.getName());
                    }

                    product.setStock(product.getStock() - cartItem.getQuantity());

                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .productName(product.getName())
                            .unitPrice(product.getPrice())
                            .imageUrl(product.getImageUrl())
                            .quantity(cartItem.getQuantity())
                            .subtotal(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                            .build();
                }).toList();

        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart();

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId){
        UUID userId = getLoggedUserId();
        Order order = findOrderOrThrow(orderId);

        if(!order.getUser().getId().equals(userId)){
            throw new BusinessRuleException("Você não tem permissão para visualizar este pedido");
        }

        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(
            OrderStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            BigDecimal minTotal,
            BigDecimal maxTotal,
            Pageable pageable){

        UUID userId = getLoggedUserId();

        Specification<Order> spec = OrderSpecification.withFilters(
                userId, status, startDate, endDate, minTotal, maxTotal
        );

        return orderRepository.findAll(spec, pageable).map(orderMapper::toResponse);
    }

    @Transactional
    public OrderResponse updateToPaid(UUID orderId){
        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() != OrderStatus.PENDING){
            throw new BusinessRuleException("Apenas pedidos PENDENTES podem ser marcados como PAGOS");
        }

        order.setStatus(OrderStatus.PAID);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateToShipped(UUID orderId, OrderTrackingRequest request){
        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() != OrderStatus.PAID){
            throw new BusinessRuleException("Apenas pedidos PAGOS podem ser despachados para a transportadora");
        }

        order.setStatus(OrderStatus.SHIPPED);
        order.setTrackingCode(request.trackingCode());
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateToDelivered(UUID orderId){
        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() != OrderStatus.SHIPPED){
            throw new BusinessRuleException("Apenas pedidos DESPACHADOS podem ser marcados como ENTREGUES.");
        }

        order.setStatus(OrderStatus.DELIVERED);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateToCanceled(UUID orderId){
        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED){
            throw new BusinessRuleException("Não é possível cancelar um pedido que já foi ENTREGUE ou que já está CANCELADO");
        }

        order.getItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
        });

        order.setStatus(OrderStatus.CANCELED);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    private UUID getLoggedUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken){
            throw new SecurityAuthenticationException("Usuário não está autenticado");
        }
        return ((JWTUserData) authentication.getPrincipal()).userId();
    }

    private Order findOrderOrThrow(UUID orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + orderId));
    }
}
