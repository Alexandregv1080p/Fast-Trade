package com.fasttrade.android

import com.fasttrade.android.data.model.Cart
import com.fasttrade.android.data.model.CartItem
import com.fasttrade.android.data.model.Coupons
import com.fasttrade.android.data.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CouponTest {

    // Carrinho: 2 x R$100 = subtotal 200, frete 10
    private fun cart(price: Double = 100.0, qty: Int = 2, fee: Double = 10.0) =
        Cart(items = listOf(CartItem(Product(price = price), qty)), deliveryFee = fee)

    @Test fun freteGratis_desconta_o_valor_do_frete() {
        assertEquals(10.0, Coupons.discountFor("FRETEGRATIS", cart(fee = 10.0))!!, 0.001)
    }

    @Test fun fast10_desconta_10_porcento_do_subtotal() {
        assertEquals(20.0, Coupons.discountFor("FAST10", cart())!!, 0.001)
    }

    @Test fun bemvindo_desconta_50_limitado_ao_subtotal() {
        assertEquals(50.0, Coupons.discountFor("BEMVINDO", cart())!!, 0.001)          // subtotal 200 -> R$50
        val pequeno = cart(price = 15.0, qty = 2)                                     // subtotal 30
        assertEquals(30.0, Coupons.discountFor("BEMVINDO", pequeno)!!, 0.001)         // limitado ao subtotal
    }

    @Test fun cupom_invalido_retorna_null() {
        assertNull(Coupons.discountFor("NAOEXISTE", cart()))
    }

    @Test fun codigo_normalizado_ignora_caixa_e_espacos() {
        assertEquals(20.0, Coupons.discountFor("  fast10 ", cart())!!, 0.001)
    }
}
