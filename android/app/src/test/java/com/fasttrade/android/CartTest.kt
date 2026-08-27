package com.fasttrade.android

import com.fasttrade.android.data.model.Cart
import com.fasttrade.android.data.model.CartItem
import com.fasttrade.android.data.model.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class CartTest {

    private fun cart(discount: Double = 0.0) = Cart(
        items = listOf(
            CartItem(Product(price = 100.0), 2),   // 200
            CartItem(Product(price = 50.0), 1)      // 50
        ),
        deliveryFee = 10.0,
        discount = discount
    )

    @Test fun subtotal_soma_preco_vezes_quantidade() {
        assertEquals(250.0, cart().subtotal, 0.001)
    }

    @Test fun total_soma_frete_sem_desconto() {
        assertEquals(260.0, cart().total, 0.001)   // 250 + 10
    }

    @Test fun total_abate_o_desconto() {
        assertEquals(240.0, cart(discount = 20.0).total, 0.001)  // 250 + 10 - 20
    }

    @Test fun carrinho_vazio_tem_total_igual_ao_frete() {
        val vazio = Cart(items = emptyList(), deliveryFee = 10.0)
        assertEquals(0.0, vazio.subtotal, 0.001)
        assertEquals(10.0, vazio.total, 0.001)
    }
}
