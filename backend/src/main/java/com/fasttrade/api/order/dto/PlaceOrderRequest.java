package com.fasttrade.api.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/** Corpo de POST /api/orders — antes era um Map&lt;String,Object&gt; cru. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)  // cliente envia deliveryAddressId (não usado aqui) — ignora
public class PlaceOrderRequest {

    @Pattern(regexp = "PIX|BOLETO|CREDIT_CARD", message = "Método de pagamento inválido")
    private String paymentMethod = "PIX";

    /** Token de cartão gerado no app (Fase 3.4). Nulo para PIX/boleto. */
    private String cardToken;
}
