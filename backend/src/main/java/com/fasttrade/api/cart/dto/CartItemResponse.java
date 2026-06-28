package com.fasttrade.api.cart.dto;

import com.fasttrade.api.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private Product product;
    private int quantity;
}
