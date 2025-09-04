package com.example.clearfootprint.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Distance {
    private final BigDecimal km;
    private Distance(BigDecimal km) {
        if (km == null || km.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Distance >= 0");
        this.km = km.setScale(6, RoundingMode.HALF_UP);
    }
    public static Distance ofKm(double km) { return new Distance(BigDecimal.valueOf(km)); }
    public BigDecimal km() { return km; }
}