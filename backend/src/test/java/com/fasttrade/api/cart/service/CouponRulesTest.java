package com.fasttrade.api.cart.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/** Regra de cupom — dinheiro, então testada de perto. Sem Spring/DB. */
class CouponRulesTest {

    private static final BigDecimal SUBTOTAL = new BigDecimal("200.00");
    private static final BigDecimal FRETE    = new BigDecimal("10.00");

    @Test
    void freteGratis_desconta_o_valor_do_frete() {
        assertThat(CouponRules.discountFor("FRETEGRATIS", SUBTOTAL, FRETE))
                .isEqualByComparingTo("10.00");
    }

    @Test
    void fast10_desconta_10_porcento_do_subtotal() {
        assertThat(CouponRules.discountFor("FAST10", SUBTOTAL, FRETE))
                .isEqualByComparingTo("20.00");
    }

    @Test
    void bemvindo_desconta_50_limitado_ao_subtotal() {
        assertThat(CouponRules.discountFor("BEMVINDO", SUBTOTAL, FRETE))
                .isEqualByComparingTo("50.00");
        assertThat(CouponRules.discountFor("BEMVINDO", new BigDecimal("30.00"), FRETE))
                .isEqualByComparingTo("30.00"); // limitado ao subtotal
    }

    @Test
    void cupom_invalido_ou_nulo_retorna_null() {
        assertThat(CouponRules.discountFor("NAOEXISTE", SUBTOTAL, FRETE)).isNull();
        assertThat(CouponRules.discountFor(null, SUBTOTAL, FRETE)).isNull();
    }

    @Test
    void codigo_normalizado_ignora_caixa_e_espacos() {
        assertThat(CouponRules.discountFor("  fast10 ", SUBTOTAL, FRETE))
                .isEqualByComparingTo("20.00");
    }
}
