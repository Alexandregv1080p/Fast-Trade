package com.fasttrade.api.order.entity;

import com.fasttrade.api.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String orderNumber;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String userName;

    // Buyer snapshot — populated on getById from User, not persisted
    @Transient private String userEmail;
    @Transient private String userPhone;
    @Transient private String userCpf;
    @Transient private String addressStreet;
    @Transient private String addressCity;
    @Transient private String addressState;
    @Transient private String addressZip;

    @Column(precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(name = "delivery_fee", precision = 10, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    private String status = "PENDING"; // PENDING|CONFIRMED|PICKING|PACKED|SHIPPED|DELIVERED|CANCELLED|REFUNDED|DEVOLUTION
    private String paymentMethod;

    /** Referência do pagamento no Mercado Pago (Fase 3.1). Null enquanto a integração estiver desligada. */
    @Column(name = "charge_id")
    private String chargeId;
    private String statusNote;

    /** Chave de idempotência (Fase 5.7): mesmo double-tap/retry não cria pedido duplicado. */
    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (orderNumber == null) {
            orderNumber = "FT" + System.currentTimeMillis();
        }
    }
}
