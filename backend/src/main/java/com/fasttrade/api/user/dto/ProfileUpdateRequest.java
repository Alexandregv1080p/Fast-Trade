package com.fasttrade.api.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/** Corpo de PUT /api/user/me (usuário edita o próprio perfil). Update parcial. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileUpdateRequest {
    private String name;
    private String phone;
    private String birthDate;
    private String addressStreet;
    private String addressCity;
    private String addressState;
    private String addressZip;
}
