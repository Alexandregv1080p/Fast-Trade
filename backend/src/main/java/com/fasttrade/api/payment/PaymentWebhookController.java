package com.fasttrade.api.payment;

import com.fasttrade.api.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Webhook de status do Mercado Pago (Fase 3.5). Rota pública (liberada no SecurityConfig);
 * a autenticidade é validada por assinatura dentro do handler, não pelo Spring Security.
 *
 * Esqueleto: hoje só confirma o recebimento (200) sem processar — assim o endpoint
 * já existe e é alcançável, mas nada muda no pedido enquanto a Fase 3.5 não for implementada.
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepo;

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody(required = false) Map<String, Object> payload) {
        // TODO(3.5): fluxo real —
        //   String status   = paymentService.resolveWebhookStatus(payload);   // valida assinatura + mapeia
        //   String chargeId = paymentService.chargeIdFromWebhook(payload);
        //   if (status != null) {
        //       orderRepo.findByChargeId(chargeId).ifPresent(o -> { o.setStatus(status); orderRepo.save(o); });
        //   }
        // Enquanto não implementado, apenas confirma o recebimento sem processar.
        return ResponseEntity.ok().build();
    }
}
