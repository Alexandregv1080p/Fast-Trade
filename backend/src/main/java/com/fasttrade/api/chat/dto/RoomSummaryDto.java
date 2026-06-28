package com.fasttrade.api.chat.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoomSummaryDto {
    private String room;
    private String userName;
    private String userEmail;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private long unreadCount;
    private String status; // NEW | WAITING | REPLIED
}
