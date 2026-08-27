package com.fasttrade.api.cart.service;

import com.fasttrade.api.cart.dto.CartItemResponse;
import com.fasttrade.api.cart.dto.CartResponse;
import com.fasttrade.api.cart.entity.CartItem;
import com.fasttrade.api.cart.repository.CartItemRepository;
import com.fasttrade.api.product.entity.Product;
import com.fasttrade.api.product.repository.ProductRepository;
import com.fasttrade.api.user.entity.User;
import com.fasttrade.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    /** Frete padrão — fonte única, usada aqui e no fechamento do pedido. */
    public static final java.math.BigDecimal DELIVERY_FEE = java.math.BigDecimal.valueOf(10.0);

    public CartResponse getCart(String email) {
        List<CartItem> items = cartItemRepo.findByUserEmailOrderByCreatedAtAsc(email);
        User user = userRepo.findByEmail(email).orElse(null);
        return buildResponse(items, user);
    }

    public CartResponse addItem(String email, Long productId, int quantity) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        CartItem item = cartItemRepo.findByUserEmailAndProductId(email, productId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setUser(user);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        item.setQuantity(item.getQuantity() + Math.max(1, quantity));
        cartItemRepo.save(item);

        return getCart(email);
    }

    public CartResponse updateItem(String email, Long productId, int quantity) {
        CartItem item = cartItemRepo.findByUserEmailAndProductId(email, productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado no carrinho"));

        if (quantity <= 0) {
            cartItemRepo.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepo.save(item);
        }

        return getCart(email);
    }

    public CartResponse removeItem(String email, Long productId) {
        cartItemRepo.deleteByUserEmailAndProductId(email, productId);
        return getCart(email);
    }

    public void clearCart(String email) {
        cartItemRepo.deleteByUserEmail(email);
    }

    /** Aplica um cupom (validado no servidor) e persiste no usuário. Erro 400 se inválido. */
    public CartResponse applyCoupon(String email, String code) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        java.util.List<CartItem> items = cartItemRepo.findByUserEmailOrderByCreatedAtAsc(email);
        java.math.BigDecimal subtotal = subtotalOf(items);
        if (CouponRules.discountFor(code, subtotal, DELIVERY_FEE) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cupom inválido");
        }
        user.setCouponCode(code.trim().toUpperCase());
        userRepo.save(user);
        return buildResponse(items, user);
    }

    public CartResponse removeCoupon(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        user.setCouponCode(null);
        userRepo.save(user);
        return getCart(email);
    }

    private java.math.BigDecimal subtotalOf(List<CartItem> items) {
        java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;
        for (CartItem ci : items) {
            java.math.BigDecimal price = ci.getProduct().getPrice() != null
                    ? ci.getProduct().getPrice() : java.math.BigDecimal.ZERO;
            subtotal = subtotal.add(price.multiply(java.math.BigDecimal.valueOf(ci.getQuantity())));
        }
        return subtotal;
    }

    private CartResponse buildResponse(List<CartItem> items, User user) {
        List<CartItemResponse> responses = items.stream()
                .map(i -> new CartItemResponse(i.getProduct(), i.getQuantity()))
                .collect(Collectors.toList());
        Object address = null;
        if (user != null && user.getAddressStreet() != null && !user.getAddressStreet().isBlank()) {
            address = java.util.Map.of(
                "street",  user.getAddressStreet(),
                "city",    java.util.Objects.requireNonNullElse(user.getAddressCity(), ""),
                "state",   java.util.Objects.requireNonNullElse(user.getAddressState(), ""),
                "zipCode", java.util.Objects.requireNonNullElse(user.getAddressZip(), ""),
                "number",  "",
                "neighborhood", ""
            );
        }

        String coupon = user != null ? user.getCouponCode() : null;
        java.math.BigDecimal subtotal = subtotalOf(items);
        java.math.BigDecimal discount = CouponRules.discountFor(coupon, subtotal, DELIVERY_FEE);
        if (discount == null) discount = java.math.BigDecimal.ZERO;

        return new CartResponse(
                responses,
                DELIVERY_FEE.doubleValue(),
                discount.doubleValue(),
                "3-5 dias úteis",
                address,
                (discount.signum() > 0) ? coupon : null
        );
    }
}
