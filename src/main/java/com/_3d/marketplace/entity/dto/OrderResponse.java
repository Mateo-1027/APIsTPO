package com._3d.marketplace.entity.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private Long id;
    private LocalDateTime date;
    private Double total;
    private String username;
}
