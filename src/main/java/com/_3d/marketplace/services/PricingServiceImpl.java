package com._3d.marketplace.services;

import com._3d.marketplace.entity.dto.PriceEstimateRequest;
import com._3d.marketplace.entity.dto.PriceEstimateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Tasación de referencia para impresiones 3D.
 *
 * Fórmula: precioSugerido = (costoMaterial + costoTiempo) + margen
 *   costoMaterial = (gramos / 1000) * precioPorKg(material)
 *   costoTiempo   = horasDeImpresion * costoPorHora   (luz + desgaste de máquina)
 *   margen        = costoProduccion * (margen% / 100)
 *
 * costoPorHora y margen por defecto son configurables desde application.properties.
 */
@Service
public class PricingServiceImpl implements PricingService {

    @Value("${pricing.cost-per-hour:500.0}")
    private double costPerHour;

    @Value("${pricing.default-margin:30.0}")
    private double defaultMargin;

    @Override
    public PriceEstimateResponse estimatePrice(PriceEstimateRequest request) {
        if (request.getMaterial() == null) {
            throw new IllegalArgumentException("Debe indicar el material.");
        }
        if (request.getWeightGrams() == null || request.getWeightGrams() <= 0) {
            throw new IllegalArgumentException("El peso en gramos debe ser mayor a 0.");
        }

        double weightGrams = request.getWeightGrams();

        double printHours = request.getPrintHours() != null ? request.getPrintHours() : 0.0;
        if (printHours < 0) {
            throw new IllegalArgumentException("Las horas de impresión no pueden ser negativas.");
        }

        double marginPct = request.getMarginPercentage() != null ? request.getMarginPercentage() : defaultMargin;
        if (marginPct < 0) {
            throw new IllegalArgumentException("El margen de ganancia no puede ser negativo.");
        }

        double materialCost = (weightGrams / 1000.0) * request.getMaterial().getPricePerKg();
        double printTimeCost = printHours * costPerHour;
        double productionCost = materialCost + printTimeCost;
        double marginAmount = productionCost * (marginPct / 100.0);
        double suggestedPrice = productionCost + marginAmount;

        return PriceEstimateResponse.builder()
                .material(request.getMaterial())
                .weightGrams(weightGrams)
                .printHours(printHours)
                .materialCost(round(materialCost))
                .printTimeCost(round(printTimeCost))
                .productionCost(round(productionCost))
                .marginPercentage(marginPct)
                .marginAmount(round(marginAmount))
                .suggestedPrice(round(suggestedPrice))
                .note("Precio de referencia orientativo. El vendedor decide el precio final de venta.")
                .build();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
