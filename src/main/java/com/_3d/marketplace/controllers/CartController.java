package com._3d.marketplace.controllers;

import com._3d.marketplace.entity.dto.CartItemRequest;
import com._3d.marketplace.entity.dto.CartResponse;
import com._3d.marketplace.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cart")
public class CartController {

    @Autowired
    private CartService cartService;


    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("username") String username) {
        return ResponseEntity.ok(cartService.getCartByUsername(username));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@RequestHeader("username") String username, @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(username, request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @RequestHeader("username") String username,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(username, itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(
            @RequestHeader("username") String username,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(username, itemId));
    }
    
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("username") String username) {
        cartService.clearCart(username);
        return ResponseEntity.noContent().build();
    }
}
