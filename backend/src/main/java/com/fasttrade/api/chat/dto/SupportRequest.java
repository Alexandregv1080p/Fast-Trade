package com.fasttrade.api.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/** Corpo de POST /api/chat/support/request. Campos opcionais (têm default no handler). */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupportRequest {
    private String email;
    private String name;
}
