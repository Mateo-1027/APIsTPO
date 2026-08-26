package com._3d.marketplace.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartResponse {
    private Long id;
    private String username;
    private List<CartItemResponse> items;
    private Double total;
}
