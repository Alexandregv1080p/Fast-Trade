package com.fasttrade.api.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Corpo de POST /api/user/change-password. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePasswordRequest {
    @NotBlank(message = "Informe a senha atual")
    private String currentPassword;
    @NotBlank(message = "Informe a nova senha")
    @Size(min = 6, message = "A nova senha deve ter ao menos 6 caracteres")
    private String newPassword;
}
