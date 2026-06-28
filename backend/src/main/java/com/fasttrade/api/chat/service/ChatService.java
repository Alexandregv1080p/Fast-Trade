package com.fasttrade.api.chat.service;

import com.fasttrade.api.chat.dto.ChatMessageDto;
import com.fasttrade.api.chat.dto.RoomSummaryDto;
import com.fasttrade.api.chat.entity.ChatMessage;
import com.fasttrade.api.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository repo;

    public ChatMessageDto save(String room, Long senderId, String senderName,
                               String senderRole, String content, ChatMessage.MessageType type) {
        ChatMessage msg = ChatMessage.builder()
                .room(room)
                .senderId(senderId)
                .senderName(senderName)
                .senderRole(senderRole)
                .content(content)
                .type(type)
                .sentAt(LocalDateTime.now())
                .readByAdmin(senderRole.equals("ADMIN") || senderRole.equals("MANAGER") || senderRole.equals("SUPPORT"))
                .build();
        return toDto(repo.save(msg));
    }

    public List<ChatMessageDto> getHistory(String room) {
        return repo.findTop100ByRoomOrderBySentAtAsc(room)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<String> getSupportRooms() {
        return repo.findDistinctSupportRooms();
    }

    public List<RoomSummaryDto> getRoomsWithDetails() {
        return repo.findDistinctSupportRooms().stream().map(room -> {
            RoomSummaryDto dto = new RoomSummaryDto();
            dto.setRoom(room);
            dto.setUserEmail(room.startsWith("support-") ? room.substring(8) : room);

            // User name from first CUSTOMER message
            repo.findFirstByRoomAndSenderRoleOrderBySentAtAsc(room, "CUSTOMER")
                .ifPresent(m -> dto.setUserName(m.getSenderName()));
            if (dto.getUserName() == null) dto.setUserName(dto.getUserEmail());

            // Last message + derived status
            repo.findFirstByRoomOrderBySentAtDesc(room).ifPresent(m -> {
                dto.setLastMessage(m.getContent());
                dto.setLastMessageAt(m.getSentAt());
                String role = m.getSenderRole();
                if ("CUSTOMER".equals(role)) dto.setStatus("WAITING");
                else if ("ADMIN".equals(role) || "SUPPORT".equals(role) || "MANAGER".equals(role)) dto.setStatus("REPLIED");
                else dto.setStatus("NEW");
            });
            if (dto.getStatus() == null) dto.setStatus("NEW");

            dto.setUnreadCount(repo.countByRoomAndReadByAdminFalseAndSenderRoleNot(room, "ADMIN"));
            return dto;
        }).collect(Collectors.toList());
    }

    public long countUnread(String room) {
        return repo.countByRoomAndReadByAdminFalseAndSenderRoleNot(room, "ADMIN");
    }

    public long countAllSupportUnread() {
        return repo.countByRoomLikeAndReadByAdminFalseAndSenderRoleNot("support-%", "ADMIN");
    }

    public void markRoomRead(String room) {
        repo.findTop100ByRoomOrderBySentAtAsc(room).forEach(m -> {
            if (!m.isReadByAdmin()) { m.setReadByAdmin(true); repo.save(m); }
        });
    }

    // ── Direct user-to-user chat ─────────────────────────────────────────────

    public static String directRoom(Long a, Long b) {
        return "direct_" + Math.min(a, b) + "_" + Math.max(a, b);
    }

    public ChatMessageDto sendDirect(Long senderId, String senderName, Long receiverId, String content) {
        String room = directRoom(senderId, receiverId);
        return save(room, senderId, senderName, "USER", content, ChatMessage.MessageType.TEXT);
    }

    public List<ChatMessageDto> getDirectHistory(String room) {
        return getHistory(room);
    }

    public List<com.fasttrade.api.chat.dto.ConversationSummaryDto> getDirectConversations(Long userId,
            com.fasttrade.api.user.repository.UserRepository userRepo) {
        // Filtragem exata: room = "direct_{a}_{b}", user participa se a==userId ou b==userId
        String uid = String.valueOf(userId);
        List<String> rooms = repo.findAllDirectRooms().stream()
            .filter(r -> {
                String[] p = r.split("_");
                return p.length == 3 && (p[1].equals(uid) || p[2].equals(uid));
            })
            .collect(Collectors.toList());
        return rooms.stream().map(room -> {
            com.fasttrade.api.chat.dto.ConversationSummaryDto dto = new com.fasttrade.api.chat.dto.ConversationSummaryDto();
            dto.setRoom(room);
            // Extract the other user's ID from room name "direct_{a}_{b}"
            String[] parts = room.split("_");
            Long otherUserId = Long.parseLong(parts[1].equals(String.valueOf(userId)) ? parts[2] : parts[1]);
            dto.setOtherUserId(otherUserId);
            userRepo.findById(otherUserId).ifPresent(u -> dto.setOtherUserName(u.getName()));
            repo.findFirstByRoomOrderBySentAtDesc(room).ifPresent(m -> {
                dto.setLastMessage(m.getContent());
                dto.setLastMessageAt(m.getSentAt() != null ? m.getSentAt().toString() : "");
            });
            dto.setUnreadCount((int) repo.countByRoomAndReadByAdminFalseAndSenderRoleNot(room, "USER"));
            return dto;
        }).collect(Collectors.toList());
    }

    private ChatMessageDto toDto(ChatMessage m) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(m.getId());
        dto.setRoom(m.getRoom());
        dto.setSenderId(m.getSenderId());
        dto.setSenderName(m.getSenderName());
        dto.setSenderRole(m.getSenderRole());
        dto.setContent(m.getContent());
        dto.setType(m.getType());
        dto.setSentAt(m.getSentAt());
        dto.setReadByAdmin(m.isReadByAdmin());
        return dto;
    }
}
