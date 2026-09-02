package com._3d.marketplace.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Double discount;
    private String categoryName;
    private List<String> imageUrls;
    private Long sellerId;
    private String sellerName;
}
