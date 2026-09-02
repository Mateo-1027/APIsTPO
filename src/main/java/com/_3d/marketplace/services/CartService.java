package com._3d.marketplace.services;

import com._3d.marketplace.entity.Cart;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.CartItemRequest;
import com._3d.marketplace.entity.dto.CartResponse;

public interface CartService {
    CartResponse getCart(User user);
    CartResponse addItemToCart(User user, CartItemRequest request);
    CartResponse updateItemQuantity(User user, Long itemId, Integer quantity);
    CartResponse removeItemFromCart(User user, Long itemId);
    void clearCart(User user);

    Cart getRawCart(User user);
}
