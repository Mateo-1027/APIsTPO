package com._3d.marketplace.services;

import com._3d.marketplace.entity.dto.PriceEstimateRequest;
import com._3d.marketplace.entity.dto.PriceEstimateResponse;

public interface PricingService {
    PriceEstimateResponse estimatePrice(PriceEstimateRequest request);
}
