package com.fasttrade.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Taxas da plataforma, fonte única (application.yml → app.fees.*). */
@Component
public class FeeConfig {

    private final BigDecimal commissionRate;
    private final BigDecimal tradeTaxRate;

    public FeeConfig(
            @Value("${app.fees.commission-rate:0.05}") double commissionRate,
            @Value("${app.fees.trade-tax-rate:0.02}") double tradeTaxRate) {
        this.commissionRate = BigDecimal.valueOf(commissionRate);
        this.tradeTaxRate = BigDecimal.valueOf(tradeTaxRate);
    }

    public BigDecimal commissionRate() { return commissionRate; }

    public BigDecimal tradeTaxRate() { return tradeTaxRate; }

    /** Comissão da plataforma sobre um preço. */
    public BigDecimal commissionOn(BigDecimal price) {
        if (price == null) return BigDecimal.ZERO;
        return price.multiply(commissionRate).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /** Taxa de troca sobre um subtotal. */
    public BigDecimal taxOn(BigDecimal subtotal) {
        if (subtotal == null) return BigDecimal.ZERO;
        return subtotal.multiply(tradeTaxRate).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
