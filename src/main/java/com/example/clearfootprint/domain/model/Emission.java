package com.example.clearfootprint.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Emission {
    private final BigDecimal gram;
    private Emission(BigDecimal g) {
        if (g == null || g.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Emission >= 0");
        this.gram = g.setScale(6, RoundingMode.HALF_UP);
    }
    public static Emission ofGram(double g) { return new Emission(BigDecimal.valueOf(g)); }
    public BigDecimal gram() { return gram; }
    public Emission plus(Emission other) { return new Emission(this.gram.add(other.gram)); }
}