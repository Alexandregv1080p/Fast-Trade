package com.fasttrade.api.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private String cpfCnpj;
    private String phone;
    private String birthDate;
    private String gender;

    private Integer score = 0;
    @Column(name = "trades")
    private Integer trades = 0;
    private Boolean blocked = false;

    // Address inline
    private String addressStreet;
    private String addressCity;
    private String addressState;
    private String addressZip;

    // Cupom aplicado no carrinho (limpo ao finalizar o pedido)
    private String couponCode;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
