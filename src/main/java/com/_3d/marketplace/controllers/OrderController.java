package com._3d.marketplace.controllers;

import com._3d.marketplace.entity.dto.OrderResponse;
import com._3d.marketplace.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@RequestHeader("username") String username) {
        return ResponseEntity.ok(orderService.checkout(username));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getHistory(@RequestHeader("username") String username) {
        return ResponseEntity.ok(orderService.getHistory(username));
    }
}
