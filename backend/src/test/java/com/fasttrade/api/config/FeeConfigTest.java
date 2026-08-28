package com.fasttrade.api.config;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/** Comissão e taxa de troca — arredondamento e nulos. Sem Spring/DB. */
class FeeConfigTest {

    private final FeeConfig fees = new FeeConfig(0.05, 0.02); // 5% comissão, 2% taxa

    @Test
    void comissao_5_porcento_arredonda_2_casas() {
        assertThat(fees.commissionOn(new BigDecimal("100.00"))).isEqualByComparingTo("5.00");
        assertThat(fees.commissionOn(new BigDecimal("19.99"))).isEqualByComparingTo("1.00"); // 0.9995 -> 1.00 HALF_UP
    }

    @Test
    void taxa_2_porcento_sobre_subtotal() {
        assertThat(fees.taxOn(new BigDecimal("250.00"))).isEqualByComparingTo("5.00");
    }

    @Test
    void nulo_vira_zero() {
        assertThat(fees.commissionOn(null)).isEqualByComparingTo("0");
        assertThat(fees.taxOn(null)).isEqualByComparingTo("0");
    }
}
