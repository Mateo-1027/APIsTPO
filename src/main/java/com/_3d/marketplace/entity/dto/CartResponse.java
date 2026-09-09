package com._3d.marketplace.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
public class CartResponse {
    private Long id;
    private String email;
    private List<CartItemResponse> items;
    private Double total;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
}
