package com._3d.marketplace.entity.dto;

import lombok.Data;

@Data
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private Boolean productActive;
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;
    private Long sellerId;
    private String sellerName;
}
