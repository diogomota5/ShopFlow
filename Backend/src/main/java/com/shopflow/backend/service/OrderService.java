package com.shopflow.backend.service;

import com.shopflow.backend.config.JWTUserData;
import com.shopflow.backend.dto.request.OrderRequest;
import com.shopflow.backend.dto.request.OrderTrackingRequest;
import com.shopflow.backend.dto.response.OrderResponse;
import com.shopflow.backend.dto.response.DashboardResponse;
import com.shopflow.backend.dto.response.OrderStatusHistoryResponse;
import com.shopflow.backend.entity.*;
import com.shopflow.backend.entity.enums.OrderStatus;
import com.shopflow.backend.entity.enums.StockMovementType;
import com.shopflow.backend.exception.BusinessRuleException;
import com.shopflow.backend.exception.SecurityAuthenticationException;
import com.shopflow.backend.mapper.OrderMapper;
import com.shopflow.backend.repository.AddressesRepository;
import com.shopflow.backend.repository.CouponRepository;
import com.shopflow.backend.repository.OrderRepository;
import com.shopflow.backend.repository.ProductRepository;
import com.shopflow.backend.repository.CategoryRepository;
import com.shopflow.backend.repository.StockMovementRepository;
import com.shopflow.backend.repository.OrderStatusHistoryRepository;
import com.shopflow.backend.specification.OrderSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartService cartService;
    private final AddressesRepository addressesRepository;
    private final CouponService couponService;
    private final CouponRepository couponRepository;
    private final OrderMapper orderMapper;
    private final StockMovementRepository stockMovementRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final EmailService emailService;

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

            if (couponRepository.consumeIfAvailable(coupon.getId()) == 0) {
                throw new BusinessRuleException("Este cupom atingiu o limite de utilizações");
            }
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

                    if (productRepository.reserveStock(product.getId(), cartItem.getQuantity()) == 0) {
                        throw new BusinessRuleException("Estoque insuficiente para o produto: " + product.getName());
                    }

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
        recordStockMovements(savedOrder, StockMovementType.RESERVATION);
        recordStatus(savedOrder, OrderStatus.PENDING, "Pedido criado");
        emailService.sendOrderStatusEmail(savedOrder, OrderStatus.PENDING);

        cartService.clearCart();

        return toResponseWithHistory(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId){
        UUID userId = getLoggedUserId();
        Order order = findOrderOrThrow(orderId);

        if(!order.getUser().getId().equals(userId)){
            throw new BusinessRuleException("Você não tem permissão para visualizar este pedido");
        }

        return toResponseWithHistory(order);
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

        return orderRepository.findAll(spec, pageable).map(this::toResponseWithHistory);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(OrderStatus status, Pageable pageable) {
        return orderRepository.findAll(OrderSpecification.withFilters(null, status, null, null, null, null), pageable)
            .map(this::toResponseWithHistory);
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardSummary(LocalDateTime start, LocalDateTime end) {
        var topProducts = orderRepository.findTopProducts(start, end, PageRequest.of(0, 5)).stream()
                .map(item -> new DashboardResponse.DashboardTopProduct(item.getName(), item.getQuantity()))
                .toList();
        var sales = orderRepository.findDailySales(start, end).stream()
                .map(item -> new DashboardResponse.DashboardDailySales(item.getDate().toString(), item.getTotal()))
                .toList();
        return new DashboardResponse(productRepository.countByActiveTrue(), categoryRepository.count(), orderRepository.countForDashboard(start, end),
                orderRepository.countForDashboardByStatus(OrderStatus.PENDING, start, end), orderRepository.sumCompletedRevenue(start, end), topProducts, sales);
    }

    @Transactional
    public OrderResponse updateToPaid(UUID orderId){
        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() != OrderStatus.PENDING){
            throw new BusinessRuleException("Apenas pedidos PENDENTES podem ser marcados como PAGOS");
        }

        order.setStatus(OrderStatus.PAID);
        Order updatedOrder = orderRepository.save(order);
        recordStatus(updatedOrder, OrderStatus.PAID, "Pagamento confirmado");
        emailService.sendOrderStatusEmail(updatedOrder, OrderStatus.PAID);
        return toResponseWithHistory(updatedOrder);
    }

    @Transactional
    public OrderResponse updateToShipped(UUID orderId, OrderTrackingRequest request){
        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() != OrderStatus.PAID){
            throw new BusinessRuleException("Apenas pedidos PAGOS podem ser despachados para a transportadora");
        }

        order.setStatus(OrderStatus.SHIPPED);
        order.setTrackingCode(request.trackingCode());
        Order updatedOrder = orderRepository.save(order);
        recordStatus(updatedOrder, OrderStatus.SHIPPED, "Pedido despachado");
        emailService.sendOrderStatusEmail(updatedOrder, OrderStatus.SHIPPED);
        return toResponseWithHistory(updatedOrder);
    }

    @Transactional
    public OrderResponse updateToDelivered(UUID orderId){
        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() != OrderStatus.SHIPPED){
            throw new BusinessRuleException("Apenas pedidos DESPACHADOS podem ser marcados como ENTREGUES.");
        }

        order.setStatus(OrderStatus.DELIVERED);
        Order updatedOrder = orderRepository.save(order);
        recordStatus(updatedOrder, OrderStatus.DELIVERED, "Pedido entregue");
        emailService.sendOrderStatusEmail(updatedOrder, OrderStatus.DELIVERED);
        return toResponseWithHistory(updatedOrder);
    }

    @Transactional
    public OrderResponse updateToCanceled(UUID orderId){
        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED){
            throw new BusinessRuleException("Não é possível cancelar um pedido que já foi ENTREGUE ou que já está CANCELADO");
        }

        order.getItems().forEach(item -> {
            productRepository.releaseStock(item.getProduct().getId(), item.getQuantity());
        });

        order.setStatus(OrderStatus.CANCELED);
        Order canceledOrder = orderRepository.save(order);
        recordStockMovements(canceledOrder, StockMovementType.RELEASE);
        recordStatus(canceledOrder, OrderStatus.CANCELED, "Pedido cancelado");
        emailService.sendOrderStatusEmail(canceledOrder, OrderStatus.CANCELED);
        return toResponseWithHistory(canceledOrder);
    }

    @Transactional
    public OrderResponse reopenOrder(UUID orderId) {
        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() != OrderStatus.CANCELED) {
            throw new BusinessRuleException("Apenas pedidos cancelados podem ser reabertos");
        }

        order.getItems().forEach(item -> {
            if (productRepository.reserveStock(item.getProduct().getId(), item.getQuantity()) == 0) {
                throw new BusinessRuleException("Estoque insuficiente para reabrir o pedido: " + item.getProductName());
            }
        });

        order.setStatus(OrderStatus.PENDING);
        order.setTrackingCode(null);
        Order reopenedOrder = orderRepository.save(order);
        recordStockMovements(reopenedOrder, StockMovementType.RESERVATION);
        recordStatus(reopenedOrder, OrderStatus.PENDING, "Pedido reaberto");
        emailService.sendOrderStatusEmail(reopenedOrder, OrderStatus.PENDING);
        return toResponseWithHistory(reopenedOrder);
    }

    @Transactional
    public OrderResponse cancelByCustomer(UUID orderId) {
        UUID userId = getLoggedUserId();
        Order order = findOrderOrThrow(orderId);
        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessRuleException("Este pedido não pertence ao usuário logado");
        }
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PAID) {
            throw new BusinessRuleException("O pedido não pode mais ser cancelado nesta etapa");
        }
        order.getItems().forEach(item -> productRepository.releaseStock(item.getProduct().getId(), item.getQuantity()));
        order.setStatus(OrderStatus.CANCELED);
        Order canceledOrder = orderRepository.save(order);
        recordStockMovements(canceledOrder, StockMovementType.RELEASE);
        recordStatus(canceledOrder, OrderStatus.CANCELED, "Cancelado pelo cliente");
        emailService.sendOrderStatusEmail(canceledOrder, OrderStatus.CANCELED);
        return toResponseWithHistory(canceledOrder);
    }

    private void recordStockMovements(Order order, StockMovementType type) {
        order.getItems().forEach(item -> stockMovementRepository.save(StockMovement.builder()
                .product(item.getProduct())
                .order(order)
                .type(type)
                .quantity(item.getQuantity())
                .build()));
    }

    private void recordStatus(Order order, OrderStatus status, String note) {
        statusHistoryRepository.save(OrderStatusHistory.builder().order(order).status(status).note(note).build());
    }

    private OrderResponse toResponseWithHistory(Order order) {
        var history = statusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(order.getId()).stream()
                .map(item -> new OrderStatusHistoryResponse(item.getId(), item.getStatus(), item.getNote(), item.getCreatedAt()))
                .toList();
        var response = orderMapper.toResponse(order);
        return new OrderResponse(response.id(), response.status(), response.subtotal(), response.discount(), response.total(),
                response.shippingZipCode(), response.shippingStreet(), response.shippingNumber(), response.shippingComplement(),
                response.shippingNeighborhood(), response.shippingCity(), response.shippingState(), response.trackingCode(),
                response.notes(), response.items(), history, response.createdAt());
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
