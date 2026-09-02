package com._3d.marketplace.entity.dto;

import com._3d.marketplace.entity.Material;
import lombok.Builder;
import lombok.Data;

/**
 * Resultado de la tasación. Muestra el desglose para que el vendedor
 * entienda de dónde sale el precio sugerido (es orientativo).
 */
@Data
@Builder
public class PriceEstimateResponse {
    private Material material;
    private Double weightGrams;
    private Double printHours;

    private Double materialCost;       // costo del material usado
    private Double printTimeCost;      // costo del tiempo de impresión (luz + desgaste)
    private Double productionCost;      // material + tiempo
    private Double marginPercentage;   // margen aplicado
    private Double marginAmount;       // ganancia en valor
    private Double suggestedPrice;     // precio de referencia final

    private String note;
}
