package com.fasttrade.api;

import com.fasttrade.api.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

/**
 * Sobe a aplicação inteira contra um Postgres real (Testcontainers). Valida:
 *  - o wiring de todos os beans (inclui os novos: FeeConfig, PaymentService, DTOs);
 *  - que o Flyway aplica V1–V5 sem erro;
 *  - com ddl-auto=validate, que as entidades batem com o schema migrado
 *    (pega, por ex., coluna faltando pra um campo novo de entidade).
 */
class ApplicationContextIT extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
        // Se o contexto sobe, Flyway aplicou e as entidades validaram contra o schema.
    }
}
