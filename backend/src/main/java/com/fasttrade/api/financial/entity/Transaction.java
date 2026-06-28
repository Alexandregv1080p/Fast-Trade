package com.fasttrade.api.financial.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String userName;

    private String type;   // CREDIT | DEBIT
    private String status; // COMPLETED | PENDING | FAILED

    @Column(precision = 10, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    private String description;
    private String paymentMethod;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
