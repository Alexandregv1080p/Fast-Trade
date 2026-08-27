package com.fasttrade.android.data.model

/**
 * Regra de cupom, isolada como função pura para ser testável sem o ViewModel/Android.
 *
 * ponytail: resolvido client-side; mover para validação no backend (/cart/coupon) quando existir.
 */
object Coupons {

    /** Desconto em reais para o [code] sobre o [cart], ou null se o cupom não existe. */
    fun discountFor(code: String, cart: Cart): Double? = when (code.trim().uppercase()) {
        "FRETEGRATIS" -> cart.deliveryFee
        "FAST10"      -> cart.subtotal * 0.10
        "BEMVINDO"    -> minOf(50.0, cart.subtotal)
        else          -> null
    }
}
