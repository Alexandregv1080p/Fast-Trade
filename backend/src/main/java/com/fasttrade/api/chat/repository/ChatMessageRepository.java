package com.fasttrade.api.chat.repository;

import com.fasttrade.api.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findTop100ByRoomOrderBySentAtAsc(String room);

    @Query("SELECT DISTINCT m.room FROM ChatMessage m WHERE m.room LIKE 'support-%' ORDER BY m.room")
    List<String> findDistinctSupportRooms();

    long countByRoomAndReadByAdminFalseAndSenderRoleNot(String room, String senderRole);

    long countByRoomLikeAndReadByAdminFalseAndSenderRoleNot(String roomPattern, String senderRole);

    java.util.Optional<ChatMessage> findFirstByRoomOrderBySentAtDesc(String room);

    java.util.Optional<ChatMessage> findFirstByRoomAndSenderRoleOrderBySentAtAsc(String room, String senderRole);

    /** Todas as salas de chat direto (filtragem exata feita no service) */
    @Query("SELECT DISTINCT m.room FROM ChatMessage m WHERE m.room LIKE 'direct_%'")
    List<String> findAllDirectRooms();

    java.util.Optional<ChatMessage> findFirstByRoomAndSenderIdNotOrderBySentAtDesc(String room, Long senderId);
}
