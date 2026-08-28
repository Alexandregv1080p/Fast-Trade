package com.fasttrade.api.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Credenciais/ambiente do PagBank, vindos de env/Secret (app.pagbank.*). */
@Component
public class PagBankConfig {

    private final String baseUrl;
    private final String token;
    private final boolean sandbox;

    public PagBankConfig(
            @Value("${app.pagbank.base-url:}") String baseUrl,
            @Value("${app.pagbank.token:}") String token,
            @Value("${app.pagbank.sandbox:true}") boolean sandbox) {
        this.baseUrl = baseUrl;
        this.token = token;
        this.sandbox = sandbox;
    }

    public String baseUrl() { return baseUrl; }

    public String token() { return token; }

    public boolean sandbox() { return sandbox; }

    /** Integração só liga quando há token configurado. Sem token, o fluxo simulado continua. */
    public boolean enabled() { return token != null && !token.isBlank(); }
}
