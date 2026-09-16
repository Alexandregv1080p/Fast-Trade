package com.fasttrade.api.payment;

import com.fasttrade.api.order.entity.Order;
import com.fasttrade.api.payment.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Ponto único de integração com o Mercado Pago (Fase 3.1). Esqueleto: os métodos ainda
 * não chamam a API — cada TODO marca onde entra o RestClient quando o access token de
 * TESTE chegar. Enquanto {@link MercadoPagoConfig#enabled()} for false, ninguém chama
 * isto e o app segue no fluxo simulado.
 *
 * API base: POST {baseUrl}/v1/payments (Payments API), com Authorization: Bearer {accessToken}.
 * Recomenda-se enviar o header X-Idempotency-Key (casa com a chave da Fase 5.7).
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final MercadoPagoConfig config;

    /**
     * Cria o pagamento no Mercado Pago conforme o método do pedido.
     *
     * @param method    "PIX" | "BOLETO" | "CREDIT_CARD"
     * @param cardToken token de cartão gerado no app via SDK/MP.js (só p/ CREDIT_CARD; null nos demais)
     */
    public PaymentResult createCharge(Order order, String method, String cardToken) {
        // TODO(3.1): montar o RestClient autenticado uma vez e reusar nos métodos abaixo.
        //   RestClient client = RestClient.builder()
        //       .baseUrl(config.baseUrl())
        //       .defaultHeader("Authorization", "Bearer " + config.accessToken())
        //       .defaultHeader("X-Idempotency-Key", order.getIdempotencyKey())
        //       .build();
        return switch (method == null ? "" : method.toUpperCase()) {
            case "PIX"         -> createPixPayment(order);
            case "BOLETO"      -> createBoletoPayment(order);
            case "CREDIT_CARD" -> createCardPayment(order, cardToken);
            default -> throw new IllegalArgumentException("Método de pagamento desconhecido: " + method);
        };
    }

    /** 3.2 — pagamento PIX. Body: transaction_amount, payment_method_id="pix", payer.email. */
    private PaymentResult createPixPayment(Order order) {
        // TODO(3.2): POST /v1/payments e mapear a resposta:
        //   pixCopyPaste = point_of_interaction.transaction_data.qr_code
        //   pixQrCodeUrl = data URI de point_of_interaction.transaction_data.qr_code_base64 (ou null)
        //   chargeId     = id (Long) do pagamento;  status = status ("pending"/"approved"/...)
        //   return new PaymentResult(chargeId, status, pixCopyPaste, pixQrCodeUrl, null, null);
        throw new UnsupportedOperationException("PIX Mercado Pago não implementado — ver Fase 3.2");
    }

    /** 3.3 — boleto. Body: payment_method_id="bolbradesco", payer com nome/CPF completos. */
    private PaymentResult createBoletoPayment(Order order) {
        // TODO(3.3): POST /v1/payments e mapear:
        //   boletoLine = barcode.content (linha digitável)
        //   boletoUrl  = transaction_details.external_resource_url (PDF do boleto)
        //   return new PaymentResult(chargeId, status, null, null, boletoLine, boletoUrl);
        throw new UnsupportedOperationException("Boleto Mercado Pago não implementado — ver Fase 3.3");
    }

    /** 3.4 — cartão com o token gerado no app (nunca PAN/CVV). */
    private PaymentResult createCardPayment(Order order, String cardToken) {
        // TODO(3.4): POST /v1/payments com { token, installments, payment_method_id, payer.email }.
        //   return PaymentResult.card(chargeId, status);
        throw new UnsupportedOperationException("Cartão Mercado Pago não implementado — ver Fase 3.4");
    }

    /**
     * 3.5 — processa o webhook do Mercado Pago e devolve o status já normalizado para o
     * pedido (PENDING/CONFIRMED/CANCELLED). Retorna null quando o evento não deve mudar nada.
     */
    public String resolveWebhookStatus(java.util.Map<String, Object> payload) {
        // TODO(3.5): o MP notifica com type/topic="payment" e data.id. Buscar o pagamento
        //   (GET /v1/payments/{id}) e mapear o status:
        //     approved            -> CONFIRMED
        //     pending/in_process  -> PENDING
        //     rejected/cancelled  -> CANCELLED
        throw new UnsupportedOperationException("Webhook Mercado Pago não implementado — ver Fase 3.5");
    }

    /** Extrai o id do pagamento (data.id) do payload do webhook, para achar o pedido. */
    public String chargeIdFromWebhook(java.util.Map<String, Object> payload) {
        // TODO(3.5): extrair data.id do corpo do evento do Mercado Pago.
        throw new UnsupportedOperationException("Webhook Mercado Pago não implementado — ver Fase 3.5");
    }
}
