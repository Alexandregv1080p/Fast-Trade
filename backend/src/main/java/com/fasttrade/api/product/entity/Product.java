package com.fasttrade.api.product.entity;

import com.fasttrade.api.category.entity.Category;
import com.fasttrade.api.category.entity.Subcategory;
import com.fasttrade.api.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    /** Comissão da plataforma — apenas exibição, calculada no serviço a partir da config. */
    @Transient
    private BigDecimal commission;

    private Integer stock = 0;
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    private String sku;
    private String condition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User seller;

    /** Exposto no JSON sem expor o objeto User completo */
    @jakarta.persistence.Transient
    @com.fasterxml.jackson.annotation.JsonProperty("sellerInfo")
    public SellerInfo getSellerInfo() {
        if (seller == null) return null;
        return new SellerInfo(
            seller.getId(),
            seller.getName(),
            seller.getPhone(),
            java.util.Objects.requireNonNullElse(seller.getScore(), 0),
            java.util.Objects.requireNonNullElse(seller.getTrades(), 0)
        );
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class SellerInfo {
        private Long id;
        private String name;
        private String phone;
        private int score;
        private int trades;
    }

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
