package com.fasttrade.api.cart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Corpo de POST /api/cart/coupon. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CouponRequest {

    @NotBlank(message = "Informe o cupom")
    private String code;
}
