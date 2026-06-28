package com.fasttrade.api.auth.dto;

import lombok.Data;

@Data
public class RecoveryRequest {
    private String email;
    private String userType;
}
