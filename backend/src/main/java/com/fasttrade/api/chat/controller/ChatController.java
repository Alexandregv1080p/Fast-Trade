package com.fasttrade.api.chat.controller;

import com.fasttrade.api.chat.dto.ChatMessageDto;
import com.fasttrade.api.chat.dto.ConversationSummaryDto;
import com.fasttrade.api.chat.dto.RoomSummaryDto;
import com.fasttrade.api.chat.entity.ChatMessage.MessageType;
import com.fasttrade.api.chat.service.ChatService;
import com.fasttrade.api.admin.entity.AdminUser;
import com.fasttrade.api.admin.repository.AdminUserRepository;
import com.fasttrade.api.user.entity.User;
import com.fasttrade.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate broker;
    private final ChatService chatService;
    private final AdminUserRepository adminUserRepo;
    private final UserRepository userRepo;

    // ── STOMP: inbound messages ──────────────────────────────────────────────

    @MessageMapping("/chat.send")
    public void handleMessage(@Payload Map<String, String> payload, Principal principal) {
        String room    = payload.getOrDefault("room", "internal");
        String content = payload.getOrDefault("content", "");

        String senderName, senderRole;
        Long senderId;

        String principalName = principal != null ? principal.getName() : null;
        boolean isAdmin = principalName != null && !principalName.equals("anonymousUser");

        if (isAdmin) {
            AdminUser sender = adminUserRepo.findByEmail(principalName).orElse(null);
            senderName = sender != null ? sender.getName() : principalName;
            senderRole = sender != null ? sender.getRole() : "ADMIN";
            senderId   = sender != null ? sender.getId()   : 0L;
        } else {
            // Customer — name comes in payload
            senderName = payload.getOrDefault("senderName", "Cliente");
            senderRole = "CUSTOMER";
            senderId   = 0L;
        }

        ChatMessageDto dto = chatService.save(room, senderId, senderName, senderRole, content, MessageType.TEXT);
        broker.convertAndSend("/topic/" + room, dto);

        // Notify admin panel of incoming customer message
        if ("CUSTOMER".equals(senderRole)) {
            broker.convertAndSend("/topic/support.new",
                    Map.of("room", room, "senderName", senderName, "content", content));
        }
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload Map<String, String> payload, Principal principal) {
        String room = payload.getOrDefault("room", "internal");
        String user = (principal != null) ? principal.getName() : payload.getOrDefault("senderName", "Cliente");
        broker.convertAndSend("/topic/" + room + ".typing",
                Map.of("user", user, "typing", payload.getOrDefault("typing", "false")));
    }

    // ── REST: admin endpoints ────────────────────────────────────────────────

    @GetMapping("/history/{room}")
    public ResponseEntity<List<ChatMessageDto>> history(@PathVariable String room) {
        return ResponseEntity.ok(chatService.getHistory(room));
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<String>> rooms() {
        return ResponseEntity.ok(chatService.getSupportRooms());
    }

    @GetMapping("/rooms/details")
    public ResponseEntity<List<RoomSummaryDto>> roomsDetails() {
        return ResponseEntity.ok(chatService.getRoomsWithDetails());
    }

    @GetMapping("/unread")
    public ResponseEntity<Map<String, Long>> unread() {
        long internal = chatService.countUnread("internal");
        long support  = chatService.countAllSupportUnread();
        return ResponseEntity.ok(Map.of("internal", internal, "support", support, "total", internal + support));
    }

    @PostMapping("/read/{room}")
    public ResponseEntity<Void> markRead(@PathVariable String room) {
        chatService.markRoomRead(room);
        return ResponseEntity.ok().build();
    }

    // ── REST: direct user-to-user chat ──────────────────────────────────────

    /** Envia mensagem direta para outro usuário (autenticado via JWT) */
    @PostMapping("/direct/send")
    public ResponseEntity<ChatMessageDto> sendDirect(
            @RequestBody Map<String, Object> body,
            Principal principal) {
        User sender = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED));
        Long receiverId = Long.parseLong(body.get("receiverId").toString());
        String content  = body.get("content").toString();
        ChatMessageDto dto = chatService.sendDirect(sender.getId(), sender.getName(), receiverId, content);
        return ResponseEntity.ok(dto);
    }

    /** Histórico de uma conversa direta */
    @GetMapping("/direct/{room}")
    public ResponseEntity<List<ChatMessageDto>> directHistory(
            @PathVariable String room,
            Principal principal) {
        return ResponseEntity.ok(chatService.getDirectHistory(room));
    }

    /** Lista todas as conversas do usuário logado */
    @GetMapping("/direct/conversations")
    public ResponseEntity<List<ConversationSummaryDto>> myConversations(Principal principal) {
        User sender = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED));
        return ResponseEntity.ok(chatService.getDirectConversations(sender.getId(), userRepo));
    }

    // ── REST: public customer endpoint ───────────────────────────────────────

    @PostMapping("/support/request")
    public ResponseEntity<Map<String, String>> requestSupport(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "unknown");
        String name  = body.getOrDefault("name", "Cliente");
        String room  = "support-" + email;

        // System message to open the conversation
        ChatMessageDto dto = chatService.save(room, 0L, "SISTEMA", "SYSTEM",
                name + " iniciou uma conversa de suporte.", MessageType.SYSTEM);

        // Notify admin panel
        broker.convertAndSend("/topic/support.new",
                Map.of("room", room, "senderName", name, "content", "Nova solicitação de suporte"));

        return ResponseEntity.ok(Map.of("room", room));
    }
}
