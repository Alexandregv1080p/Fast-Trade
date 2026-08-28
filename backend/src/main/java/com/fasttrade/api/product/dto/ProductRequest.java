package com.fasttrade.api.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Corpo de POST/PATCH /api/products — antes era Map&lt;String,Object&gt; cru.
 * Todos os campos são opcionais: create usa o que vier, update é parcial
 * (só grava os campos presentes). Jackson já faz a coerção de tipo.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductRequest {

    private String name;
    private String description;

    @PositiveOrZero(message = "Preço não pode ser negativo")
    private BigDecimal price;

    @PositiveOrZero(message = "Estoque não pode ser negativo")
    private Integer stock;

    private Boolean isActive;
    private String sku;
    private String condition;
    private String imageUrl;
    private Long categoryId;
    private Long subcategoryId;
}
