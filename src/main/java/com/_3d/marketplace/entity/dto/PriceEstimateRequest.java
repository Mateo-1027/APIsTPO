package com._3d.marketplace.entity.dto;

import com._3d.marketplace.entity.Material;
import lombok.Data;

@Data
public class PriceEstimateRequest {
    private Material material;
    private Double weightGrams;
    private Double printHours;
    private Double marginPercentage;
}
