package com._3d.marketplace.entity.dto;

import lombok.Data;

@Data
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double subtotal;
}
