package com.shopflow.backend.controller;

import com.shopflow.backend.dto.request.OrderRequest;
import com.shopflow.backend.dto.request.OrderTrackingRequest;
import com.shopflow.backend.dto.response.ApiResponse;
import com.shopflow.backend.dto.response.OrderResponse;
import com.shopflow.backend.dto.response.DashboardResponse;
import com.shopflow.backend.entity.enums.OrderStatus;
import com.shopflow.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request){
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Pedido realizado com sucesso", order, HttpStatus.CREATED));
    }

    @GetMapping("/search/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable UUID orderId){
        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(
                ApiResponse.success("Pedido recuperado com sucesso", order, HttpStatus.OK));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelByCustomer(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.success("Pedido cancelado com sucesso", orderService.cancelByCustomer(orderId), HttpStatus.OK));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime endDate,
            @RequestParam(required = false) BigDecimal minTotal,
            @RequestParam(required = false) BigDecimal maxTotal,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){

        Page<OrderResponse> orders = orderService.getMyOrders(status, startDate, endDate, minTotal, maxTotal, pageable);
        return ResponseEntity.ok(
                ApiResponse.success("Lista de pedidos recuperada com sucesso", orders, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/search")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Pedidos recuperados com sucesso", orderService.getAllOrders(status, pageable), HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(ApiResponse.success("Resumo do painel recuperado com sucesso", orderService.getDashboardSummary(startDate, endDate), HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/status/{orderId}/paid")
    public ResponseEntity<ApiResponse<OrderResponse>> markAsPaid(@PathVariable UUID orderId){
        OrderResponse order = orderService.updateToPaid(orderId);
        return ResponseEntity.ok(
                ApiResponse.success("Pagamento confirmado com sucesso", order, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/status/{orderId}/ship")
    public ResponseEntity<ApiResponse<OrderResponse>> MarkAsShipped(@PathVariable UUID orderId, @Valid @RequestBody OrderTrackingRequest request){
        OrderResponse order = orderService.updateToShipped(orderId, request);
        return ResponseEntity.ok(
                ApiResponse.success("Pedido despachado e código de rastreio registrado", order, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/status/{orderId}/deliver")
    public ResponseEntity<ApiResponse<OrderResponse>> markAsDelivered(@PathVariable UUID orderId){
        OrderResponse order = orderService.updateToDelivered(orderId);
        return ResponseEntity.ok(
                ApiResponse.success("Pedido marcado como entregue com sucesso", order, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/status/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> markAsCanceled(@PathVariable UUID orderId){
        OrderResponse order = orderService.updateToCanceled(orderId);
        return ResponseEntity.ok(
                ApiResponse.success("Pedido cancelado e produtos devolvidos ao estoque", order, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/status/{orderId}/reopen")
    public ResponseEntity<ApiResponse<OrderResponse>> reopenOrder(@PathVariable UUID orderId) {
        OrderResponse order = orderService.reopenOrder(orderId);
        return ResponseEntity.ok(
                ApiResponse.success("Pedido reaberto como pendente e estoque reservado novamente", order, HttpStatus.OK));
    }

}
