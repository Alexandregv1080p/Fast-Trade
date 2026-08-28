package com.fasttrade.api.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Corpo de POST /api/user/{id}/trades. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GiveTradesRequest {
    @NotNull(message = "Informe o valor")
    private Integer value;
}
