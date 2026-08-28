package com.fasttrade.api.cart.controller;

import com.fasttrade.api.cart.dto.AddToCartRequest;
import com.fasttrade.api.cart.dto.CartResponse;
import com.fasttrade.api.cart.service.CartService;
import com.fasttrade.api.user.entity.User;
import com.fasttrade.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepo;

    private String currentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart(currentUserEmail()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@RequestBody AddToCartRequest req) {
        return ResponseEntity.ok(
                cartService.addItem(currentUserEmail(), req.getProductId(),
                        req.getQuantity() != null ? req.getQuantity() : 1)
        );
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable Long productId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateItem(currentUserEmail(), productId, quantity));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(currentUserEmail(), productId));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> clearCart() {
        cartService.clearCart(currentUserEmail());
        return ResponseEntity.ok(Map.of("message", "Carrinho limpo com sucesso"));
    }

    /** Aplica um cupom de desconto (validado no servidor). */
    @PostMapping("/coupon")
    public ResponseEntity<CartResponse> applyCoupon(
            @jakarta.validation.Valid @RequestBody com.fasttrade.api.cart.dto.CouponRequest req) {
        return ResponseEntity.ok(cartService.applyCoupon(currentUserEmail(), req.getCode()));
    }

    /** Remove o cupom aplicado. */
    @DeleteMapping("/coupon")
    public ResponseEntity<CartResponse> removeCoupon() {
        return ResponseEntity.ok(cartService.removeCoupon(currentUserEmail()));
    }

    /** Atualiza endereço de entrega (persiste no perfil do usuário). Update parcial. */
    @PatchMapping("/address")
    public ResponseEntity<CartResponse> updateAddress(
            @jakarta.validation.Valid @RequestBody com.fasttrade.api.cart.dto.AddressUpdateRequest req) {
        String email = currentUserEmail();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        if (req.getStreet() != null) user.setAddressStreet(req.getStreet());
        if (req.getCity()   != null) user.setAddressCity(req.getCity());
        if (req.getState()  != null) user.setAddressState(req.getState());
        if (req.getZip()    != null) user.setAddressZip(req.getZip());
        userRepo.save(user);
        return ResponseEntity.ok(cartService.getCart(email));
    }
}
