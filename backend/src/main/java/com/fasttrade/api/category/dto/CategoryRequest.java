package com.fasttrade.api.category.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/** Corpo de POST/PATCH /api/categories. Create usa name+slug; update é parcial. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryRequest {
    private String name;
    private String slug;
}
