package com._3d.marketplace.services;

import com._3d.marketplace.entity.Cart;
import com._3d.marketplace.entity.dto.CartItemRequest;
import com._3d.marketplace.entity.dto.CartResponse;

public interface CartService {
    CartResponse getCartByUsername(String username);
    CartResponse addItemToCart(String username, CartItemRequest request);
    CartResponse updateItemQuantity(String username, Long itemId, Integer quantity);
    CartResponse removeItemFromCart(String username, Long itemId);
    void clearCart(String username);
    
    // Internal method needed by OrderService
    Cart getRawCartByUsername(String username);
}
