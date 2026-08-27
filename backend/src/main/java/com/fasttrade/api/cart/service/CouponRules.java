package com.fasttrade.api.cart.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regra de cupom — fonte da verdade. Antes vivia no app Android (client-side);
 * agora o desconto é decidido aqui e refletido no carrinho e no total do pedido.
 */
public final class CouponRules {

    private CouponRules() {}

    /** Desconto em reais para o cupom sobre o carrinho, ou {@code null} se o cupom não existe. */
    public static BigDecimal discountFor(String code, BigDecimal subtotal, BigDecimal deliveryFee) {
        if (code == null) return null;
        switch (code.trim().toUpperCase()) {
            case "FRETEGRATIS":
                return deliveryFee;
            case "FAST10":
                return subtotal.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
            case "BEMVINDO":
                return subtotal.min(BigDecimal.valueOf(50.0));
            default:
                return null;
        }
    }
}
