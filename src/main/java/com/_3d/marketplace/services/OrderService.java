package com._3d.marketplace.services;

import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.OrderResponse;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderResponse checkout(User user);
    List<OrderResponse> getHistory(User user);
    Optional<OrderResponse> getOrderById(Long orderId, User user);
    List<OrderResponse> getSales(User seller);
}
