package com._3d.marketplace.entity.dto;

import com._3d.marketplace.entity.Material;
import lombok.Data;

/**
 * Datos que carga el vendedor para pedir una tasación (precio de referencia).
 * Solo material y gramos son obligatorios; horas y margen son opcionales.
 */
@Data
public class PriceEstimateRequest {
    private Material material;         // tipo de filamento/resina usado
    private Double weightGrams;        // gramos de material usados
    private Double printHours;         // horas de impresión (opcional)
    private Double marginPercentage;   // margen de ganancia % (opcional; usa el default si no viene)
}
