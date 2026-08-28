package com.fasttrade.api.payment;

import com.fasttrade.api.order.entity.Order;
import com.fasttrade.api.payment.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Ponto único de integração com o PagBank (Fase 3.1). Esqueleto: os métodos ainda
 * não chamam a API — cada TODO marca onde entra o RestClient quando o token do
 * sandbox chegar. Enquanto {@link PagBankConfig#enabled()} for false, ninguém
 * chama isto e o app segue no fluxo simulado.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PagBankConfig config;

    /**
     * Cria a cobrança no PagBank conforme o método do pedido.
     *
     * @param order     pedido já persistido (tem itens e total)
     * @param method    "PIX" | "BOLETO" | "CREDIT_CARD"
     * @param cardToken token de cartão gerado no app (só p/ CREDIT_CARD; null nos demais)
     */
    public PaymentResult createCharge(Order order, String method, String cardToken) {
        // TODO(3.1): montar o RestClient autenticado uma vez e reusar nos métodos abaixo.
        //   RestClient client = RestClient.builder()
        //       .baseUrl(config.baseUrl())
        //       .defaultHeader("Authorization", "Bearer " + config.token())
        //       .build();
        return switch (method == null ? "" : method.toUpperCase()) {
            case "PIX"         -> createPixCharge(order);
            case "BOLETO"      -> createBoletoCharge(order);
            case "CREDIT_CARD" -> createCardCharge(order, cardToken);
            default -> throw new IllegalArgumentException("Método de pagamento desconhecido: " + method);
        };
    }

    /** 3.2 — cobrança PIX: extrair copia-e-cola + QR da resposta do PagBank. */
    private PaymentResult createPixCharge(Order order) {
        // TODO(3.2): criar a cobrança PIX no PagBank e mapear:
        //   pixCopyPaste = <texto copia-e-cola>, pixQrCodeUrl = <imagem do QR ou null>
        //   return new PaymentResult(chargeId, status, pixCopyPaste, pixQrCodeUrl, null, null);
        throw new UnsupportedOperationException("PIX PagBank não implementado — ver Fase 3.2");
    }

    /** 3.3 — cobrança boleto: extrair linha digitável + URL do PDF. */
    private PaymentResult createBoletoCharge(Order order) {
        // TODO(3.3): criar a cobrança boleto no PagBank e mapear:
        //   boletoLine = <linha digitável>, boletoUrl = <link do PDF>
        //   return new PaymentResult(chargeId, status, null, null, boletoLine, boletoUrl);
        throw new UnsupportedOperationException("Boleto PagBank não implementado — ver Fase 3.3");
    }

    /** 3.4 — cobrança de cartão com o token gerado no app (nunca PAN/CVV). */
    private PaymentResult createCardCharge(Order order, String cardToken) {
        // TODO(3.4): criar a cobrança de cartão no PagBank usando cardToken.
        //   return PaymentResult.card(chargeId, status);
        throw new UnsupportedOperationException("Cartão PagBank não implementado — ver Fase 3.4");
    }

    /**
     * 3.5 — processa o webhook de status do PagBank e devolve o status do PSP já
     * normalizado para o pedido (PENDING/CONFIRMED/CANCELLED). Retorna null quando
     * o evento não deve mudar o pedido.
     */
    public String resolveWebhookStatus(java.util.Map<String, Object> payload) {
        // TODO(3.5): validar a autenticidade do evento (assinatura do PagBank) e
        //   mapear o status recebido -> PENDING/CONFIRMED/CANCELLED.
        throw new UnsupportedOperationException("Webhook PagBank não implementado — ver Fase 3.5");
    }

    /** Extrai o id da cobrança do payload do webhook, para achar o pedido. */
    public String chargeIdFromWebhook(java.util.Map<String, Object> payload) {
        // TODO(3.5): extrair o id da cobrança do corpo do evento do PagBank.
        throw new UnsupportedOperationException("Webhook PagBank não implementado — ver Fase 3.5");
    }
}
