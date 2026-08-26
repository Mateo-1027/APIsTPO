package com._3d.marketplace.entity.dto;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long productId;
    private Integer quantity;
}
