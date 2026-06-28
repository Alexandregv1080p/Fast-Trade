package com.fasttrade.api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private Long id;
    private String name;
    private String email;
    private String role;
}
