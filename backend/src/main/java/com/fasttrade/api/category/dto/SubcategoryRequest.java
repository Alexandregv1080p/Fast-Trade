package com.fasttrade.api.category.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/** Corpo de POST/PATCH /api/subcategories. Create usa os campos; update é parcial. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubcategoryRequest {
    private String name;
    private String slug;
    private Long categoryId;
}
