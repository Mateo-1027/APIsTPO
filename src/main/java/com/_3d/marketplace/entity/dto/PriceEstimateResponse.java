package com._3d.marketplace.entity.dto;

import com._3d.marketplace.entity.Material;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceEstimateResponse {
    private Material material;
    private Double weightGrams;
    private Double printHours;

    private Double materialCost;
    private Double printTimeCost;
    private Double productionCost;
    private Double marginPercentage;
    private Double marginAmount;
    private Double suggestedPrice;

    private String note;
}
