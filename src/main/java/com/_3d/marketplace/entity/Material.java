package com._3d.marketplace.entity;

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
