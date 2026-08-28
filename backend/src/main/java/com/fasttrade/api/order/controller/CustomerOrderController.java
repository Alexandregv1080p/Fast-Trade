package com.fasttrade.api.order.controller;

import com.fasttrade.api.cart.entity.CartItem;
import com.fasttrade.api.cart.repository.CartItemRepository;
import com.fasttrade.api.order.entity.Order;
import com.fasttrade.api.order.entity.OrderItem;
import com.fasttrade.api.order.repository.OrderRepository;
import com.fasttrade.api.user.entity.User;
import com.fasttrade.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final OrderRepository orderRepo;
    private final CartItemRepository cartItemRepo;
    private final UserRepository userRepo;
    private final com.fasttrade.api.config.FeeConfig feeConfig;

    private String email() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /** Lista os pedidos do usuário logado */
    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders(Principal principal) {
        User user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        List<Order> orders = orderRepo.findByUser_IdOrderByCreatedAtDesc(user.getId());
        // enrich with user data
        orders.forEach(o -> {
            o.setUserEmail(user.getEmail());
            o.setUserPhone(user.getPhone());
        });
        return ResponseEntity.ok(orders);
    }

    /** Detalhe de um pedido */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id, Principal principal) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (user != null) {
            order.setUserEmail(user.getEmail());
            order.setAddressStreet(user.getAddressStreet());
            order.setAddressCity(user.getAddressCity());
            order.setAddressState(user.getAddressState());
            order.setAddressZip(user.getAddressZip());
        }
        return ResponseEntity.ok(order);
    }

    /** Cancela um pedido do usuário logado */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id, Principal principal) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
        User user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Apenas pedidos pendentes podem ser cancelados");
        }
        order.setStatus("CANCELLED");
        return ResponseEntity.ok(orderRepo.save(order));
    }

    /** Finaliza o carrinho e cria um pedido */
    @Transactional
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody(required = false) Map<String, Object> body,
                                            Principal principal) {
        String email = principal.getName();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        List<CartItem> cartItems = cartItemRepo.findByUserEmailOrderByCreatedAtAsc(email);
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Carrinho vazio");
        }

        // Build order
        Order order = new Order();
        order.setUser(user);
        order.setUserName(user.getName());
        order.setStatus("PENDING");
        order.setPaymentMethod(body != null && body.containsKey("paymentMethod")
                ? (String) body.get("paymentMethod") : "PIX");

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setProductName(ci.getProduct().getName());
            oi.setQuantity(ci.getQuantity());
            BigDecimal price = ci.getProduct().getPrice() != null
                    ? ci.getProduct().getPrice() : BigDecimal.ZERO;
            oi.setPrice(price);
            order.getItems().add(oi);
            subtotal = subtotal.add(price.multiply(BigDecimal.valueOf(ci.getQuantity())));
        }

        BigDecimal deliveryFee = com.fasttrade.api.cart.service.CartService.DELIVERY_FEE;
        BigDecimal discount = com.fasttrade.api.cart.service.CouponRules
                .discountFor(user.getCouponCode(), subtotal, deliveryFee);
        if (discount == null) discount = BigDecimal.ZERO;
        BigDecimal tax = feeConfig.taxOn(subtotal);
        order.setTax(tax);
        order.setDeliveryFee(deliveryFee);
        order.setTotal(subtotal.add(deliveryFee).add(tax).subtract(discount).max(BigDecimal.ZERO));

        // Ponto de integração da Fase 3.1: quando o PagBank estiver configurado,
        // criar a cobrança aqui e guardar a referência. Sem token, segue o fluxo atual.
        // if (pagBankConfig.enabled()) {
        //     PaymentResult charge = paymentService.createCharge(order, order.getPaymentMethod(), /*cardToken*/ null);
        //     order.setChargeId(charge.chargeId());
        //     // devolver dados do PIX/boleto (charge.pixCopyPaste(), charge.boletoLine(), ...) na resposta
        // }

        Order saved = orderRepo.save(order);

        // Clear cart + cupom consumido
        cartItemRepo.deleteByUserEmail(email);
        user.setCouponCode(null);
        userRepo.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
