package com._3d.marketplace.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Double discount;
    private Long categoryId;
    private List<String> imageUrls;
}
