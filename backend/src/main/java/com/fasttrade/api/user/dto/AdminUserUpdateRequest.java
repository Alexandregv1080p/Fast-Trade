package com.fasttrade.api.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/** Corpo de PATCH /api/user/{id} (admin edita um usuário). Update parcial. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminUserUpdateRequest {
    private String name;
    private String phone;
    private String cpfCnpj;
}
