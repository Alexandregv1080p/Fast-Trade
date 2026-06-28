package com.fasttrade.api.chat.dto;

import com.fasttrade.api.chat.entity.ChatMessage.MessageType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageDto {
    private Long id;
    private String room;
    private Long senderId;
    private String senderName;
    private String senderRole;
    private String content;
    private MessageType type;
    private LocalDateTime sentAt;
    private boolean readByAdmin;
}
