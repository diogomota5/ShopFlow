package com.shopflow.backend.service;

import com.shopflow.backend.config.JWTUserData;
import com.shopflow.backend.dto.request.CartItemRequest;
import com.shopflow.backend.dto.request.CartItemUpdateRequest;
import com.shopflow.backend.dto.response.CartResponse;
import com.shopflow.backend.dto.response.CartWithStatus;
import com.shopflow.backend.entity.Cart;
import com.shopflow.backend.entity.CartItem;
import com.shopflow.backend.entity.Product;
import com.shopflow.backend.entity.User;
import com.shopflow.backend.exception.BusinessRuleException;
import com.shopflow.backend.exception.SecurityAuthenticationException;
import com.shopflow.backend.mapper.CartMapper;
import com.shopflow.backend.repository.CartItemRepository;
import com.shopflow.backend.repository.CartRepository;
import com.shopflow.backend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    private Cart createCart(UUID userId){
        User user = User.builder().id(userId).build();
        Cart newCart = Cart.builder()
                .user(user)
                .build();
        return cartRepository.save(newCart);
    }

    @Transactional
    public CartResponse addItem(CartItemRequest request){
        UUID userId = getLoggedUserId();
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + request.productId()));

        if (!product.getActive()) {
            throw new BusinessRuleException("Este produto não está ativo");
        }

        if(product.getStock() < request.quantity()){
            throw new BusinessRuleException("Estoque insuficiente. Quantidade disponível: " + product.getStock());
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        int quantityToAdd = request.quantity();

        if(existingItem.isPresent()){
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantityToAdd;

            if(product.getStock() < newQuantity){
                throw new BusinessRuleException("Estoque insuficiente. Quantidade disponível: " + product.getStock());
            }
            item.setQuantity(newQuantity);
        } else {
            if(product.getStock() < quantityToAdd) {
                throw new BusinessRuleException("Estoque insuficiente. Quantidade disponível: " + product.getStock());
            }
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantityToAdd)
                    .addedAt(LocalDateTime.now())
                    .build();
            cart.getItems().add(newItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toResponse(updatedCart);
    }

    @Transactional
    public CartResponse updateItem(UUID itemId, CartItemUpdateRequest request){
        CartItem item = getCartItemBelongingToUser(itemId);

        if (item.getProduct().getStock() < request.quantity()){
            throw new BusinessRuleException("Estoque insuficiente. Disponível: " + item.getProduct().getStock());
        }

        item.setQuantity(request.quantity());
        cartItemRepository.save(item);
        return cartMapper.toResponse(item.getCart());
    }

    @Transactional
    public CartResponse removeItem(UUID itemId){
        CartItem item = getCartItemBelongingToUser(itemId);
        Cart cart = item.getCart();

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        return cartMapper.toResponse(cartRepository.findByIdWithItems(cart.getId()).orElseThrow());
    }

    @Transactional
    public void clearCart(){
        UUID userId = getLoggedUserId();
        Cart cart = getOrCreateCart(userId);

        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private UUID getLoggedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken){
            throw new SecurityAuthenticationException("Usuário não está autenticado");
        }
        JWTUserData jwtUserData = (JWTUserData) authentication.getPrincipal();
        return jwtUserData.userId();
    }

    @Transactional
    public Cart getOrCreateCart(UUID userId){
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));
    }

    private CartItem getCartItemBelongingToUser(UUID itemId){
        UUID userId = getLoggedUserId();

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado: " + itemId));

        if(!item.getCart().getUser().getId().equals(userId)){
            throw new BusinessRuleException("Este item não pertence ao seu carrinho");
        }
        return item;
    }

    @Transactional(readOnly = true)
    public CartWithStatus getMyCart(){
        UUID userId = getLoggedUserId();
        boolean[] isNew = {false};

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                   isNew[0] = true;
                   return createCart(userId);
                });

        CartResponse response = cartMapper.toResponse(cart);
        return new CartWithStatus(response, isNew[0]);
    }

}
