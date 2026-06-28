package com.fasttrade.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String credential;
    @NotBlank
    private String password;
}
