package com.fasttrade.api.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Credenciais/ambiente do Mercado Pago, vindos de env/Secret (app.mercadopago.*).
 * Use as credenciais de TESTE (access token começa com "TEST-") — sandbox não exige KYC.
 */
@Component
public class MercadoPagoConfig {

    private final String baseUrl;
    private final String accessToken;
    private final String publicKey;

    public MercadoPagoConfig(
            @Value("${app.mercadopago.base-url:https://api.mercadopago.com}") String baseUrl,
            @Value("${app.mercadopago.access-token:}") String accessToken,
            @Value("${app.mercadopago.public-key:}") String publicKey) {
        this.baseUrl = baseUrl;
        this.accessToken = accessToken;
        this.publicKey = publicKey;
    }

    public String baseUrl() { return baseUrl; }

    public String accessToken() { return accessToken; }

    /** Chave pública — vai pro app p/ tokenizar o cartão (o backend nunca vê PAN/CVV). */
    public String publicKey() { return publicKey; }

    /** Integração só liga quando há access token configurado. Sem token, o fluxo simulado continua. */
    public boolean enabled() { return accessToken != null && !accessToken.isBlank(); }
}
