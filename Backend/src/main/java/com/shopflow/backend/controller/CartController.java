package com.shopflow.backend.controller;

import com.shopflow.backend.dto.request.CartItemRequest;
import com.shopflow.backend.dto.request.CartItemUpdateRequest;
import com.shopflow.backend.dto.response.ApiResponse;
import com.shopflow.backend.dto.response.CartResponse;
import com.shopflow.backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart(){
        var cartWithStatus = cartService.getMyCart();

        String message = cartWithStatus.isNew()
                ? "Novo carrinho criado e inicializado com sucesso"
                : "Carrinho recuperado com sucesso";

        return ResponseEntity.ok(
                ApiResponse.success(message, cartWithStatus.response(), HttpStatus.OK));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(@Valid @RequestBody CartItemRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Item adicionado ao carrinho", cartService.addItem(request), HttpStatus.CREATED));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(@PathVariable UUID itemId, @Valid @RequestBody CartItemUpdateRequest request){
        return ResponseEntity.ok(
                ApiResponse.success("Item atualizado com sucesso", cartService.updateItem(itemId, request), HttpStatus.OK));
    }

    @DeleteMapping("/items/clear/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(@PathVariable UUID itemId){
        return ResponseEntity.ok(
                ApiResponse.success("Item removido do carrinho com sucesso", cartService.removeItem(itemId), HttpStatus.OK));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(){
        cartService.clearCart();
        return ResponseEntity.ok(
                ApiResponse.success("Carrinho esvaziado com sucesso", HttpStatus.OK));
    }
}
