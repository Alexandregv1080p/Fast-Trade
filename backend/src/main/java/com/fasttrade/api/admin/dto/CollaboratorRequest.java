package com.fasttrade.api.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import lombok.Data;

/** Corpo de POST/PATCH /api/admin/collaborators. Create usa todos; update é parcial. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollaboratorRequest {
    private String name;
    @Email(message = "E-mail inválido")
    private String email;
    private String password;
    private String role;
}
