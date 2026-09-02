package com._3d.marketplace.controllers;

import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.OrderResponse;
import com._3d.marketplace.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.checkout(user));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.getHistory(user));
    }
}
