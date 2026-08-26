package com._3d.marketplace.services;

import com._3d.marketplace.entity.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse checkout(String username);
    List<OrderResponse> getHistory(String username);
}
