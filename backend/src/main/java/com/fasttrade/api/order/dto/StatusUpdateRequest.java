package com.fasttrade.api.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Corpo de PATCH /api/order/{id}/status (admin). */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusUpdateRequest {

    @NotBlank(message = "Informe o status")
    private String status;

    private String note;
}
