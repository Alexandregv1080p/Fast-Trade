package com.fasttrade.api.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Corpo de POST /api/chat/direct/send. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectMessageRequest {
    @NotNull(message = "Destinatário obrigatório")
    private Long receiverId;
    @NotBlank(message = "Mensagem vazia")
    private String content;
}
