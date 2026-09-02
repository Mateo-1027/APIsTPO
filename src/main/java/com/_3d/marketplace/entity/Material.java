package com._3d.marketplace.entity;

/**
 * Materiales de impresión 3D soportados por la tasación.
 * El precio es un valor de REFERENCIA por kilogramo, en la misma moneda
 * que se usa para los productos. Ajustar según los costos reales del equipo.
 */
public enum Material {
    PLA(15000.0),
    ABS(17000.0),
    PETG(20000.0),
    TPU(30000.0),
    RESINA(40000.0);

    private final double pricePerKg;

    Material(double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public double getPricePerKg() {
        return pricePerKg;
    }
}
