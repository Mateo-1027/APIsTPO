package com._3d.marketplace.services;

import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse checkout(User user);
    List<OrderResponse> getHistory(User user);
}
