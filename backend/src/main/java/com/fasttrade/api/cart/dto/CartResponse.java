package com.fasttrade.api.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private List<CartItemResponse> items;
    private double deliveryFee = 10.0;
    private double discount = 0.0;
    private String estimatedDelivery = "3-5 dias úteis";
    private Object deliveryAddress = null;
}
