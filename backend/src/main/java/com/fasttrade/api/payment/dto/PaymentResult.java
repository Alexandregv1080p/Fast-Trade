package com.fasttrade.api.payment.dto;

/**
 * Resultado de uma cobrança criada no PSP. Os campos opcionais dependem do método:
 * PIX preenche copia-e-cola/QR; boleto preenche linha digitável/URL; cartão nenhum.
 *
 * ponytail: shape mínimo pro esqueleto — ajustar aos campos reais do PagBank ao implementar.
 */
public record PaymentResult(
        String chargeId,      // id da cobrança no PagBank (referência guardada no pedido)
        String status,        // status inicial do PSP → mapeado pro status do pedido
        String pixCopyPaste,  // PIX: texto copia-e-cola
        String pixQrCodeUrl,  // PIX: imagem do QR (ou null se gerar no app)
        String boletoLine,    // Boleto: linha digitável
        String boletoUrl      // Boleto: link do PDF/boleto
) {
    public static PaymentResult card(String chargeId, String status) {
        return new PaymentResult(chargeId, status, null, null, null, null);
    }
}
