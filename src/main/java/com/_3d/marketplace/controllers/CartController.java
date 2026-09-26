package com._3d.marketplace.controllers;

import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.CartItemQuantityRequest;
import com._3d.marketplace.entity.dto.CartItemRequest;
import com._3d.marketplace.entity.dto.CartResponse;
import com._3d.marketplace.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getCart(user));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@AuthenticationPrincipal User user, @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(user, request));
    }

    @PutMapping("/items")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @AuthenticationPrincipal User user,
            @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(user, request));
    }

    @DeleteMapping("/items") 
    public ResponseEntity<CartResponse> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId,
            @RequestBody CartItemRequest request) { 
        return ResponseEntity.ok(cartService.removeItemFromCart(user,request));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }
}
