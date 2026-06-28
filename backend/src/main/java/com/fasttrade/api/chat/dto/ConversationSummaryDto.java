package com.fasttrade.api.chat.dto;

import lombok.Data;

@Data
public class ConversationSummaryDto {
    private String room;
    private Long otherUserId;
    private String otherUserName;
    private String lastMessage;
    private String lastMessageAt;
    private int unreadCount;
}
