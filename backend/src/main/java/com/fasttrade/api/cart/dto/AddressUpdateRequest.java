package com.fasttrade.api.cart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Corpo de PATCH /api/cart/address. Update parcial: só os campos presentes (não-nulos)
 * são gravados. O app também manda number/complemento, mas o perfil ainda não os persiste
 * (ignorados aqui) — ver pendência no roteiro.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressUpdateRequest {
    private String street;
    private String city;
    private String state;
    private String zip;
}
