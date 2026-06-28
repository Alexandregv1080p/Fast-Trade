package com.fasttrade.api.user.controller;

import com.fasttrade.api.common.dto.PageResponse;
import com.fasttrade.api.user.entity.User;
import com.fasttrade.api.user.repository.UserRepository;
import com.fasttrade.api.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserRepository userRepo;

    /** Perfil do usuário logado — usado pelo app Android */
    @GetMapping("/me")
    public ResponseEntity<User> getMe(Principal principal) {
        User user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<PageResponse<User>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(service.getAll(page, pageSize, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(service.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<User> block(@PathVariable Long id) {
        return ResponseEntity.ok(service.block(id));
    }

    @PatchMapping("/{id}/unblock")
    public ResponseEntity<User> unblock(@PathVariable Long id) {
        return ResponseEntity.ok(service.unblock(id));
    }

    @PostMapping("/{id}/trades")
    public ResponseEntity<User> giveTrades(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(service.giveTrades(id, body.get("value")));
    }

    /** Atualiza dados do próprio perfil */
    @PutMapping("/me")
    public ResponseEntity<User> updateMe(@RequestBody Map<String, Object> data, Principal principal) {
        return ResponseEntity.ok(service.updateSelf(principal.getName(), data));
    }

    /** Troca de senha */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody Map<String, String> body, Principal principal) {
        service.changePassword(principal.getName(),
                body.get("currentPassword"), body.get("newPassword"));
        return ResponseEntity.ok(java.util.Map.of("message", "Senha alterada com sucesso"));
    }

    /** Perfil público do vendedor — não requer autenticação */
    @GetMapping("/{id}/public")
    public ResponseEntity<PublicSellerResponse> getPublicProfile(@PathVariable Long id) {
        User u = service.getById(id);
        return ResponseEntity.ok(new PublicSellerResponse(
            u.getId(), u.getName(), u.getPhone(),
            java.util.Objects.requireNonNullElse(u.getScore(), 0),
            java.util.Objects.requireNonNullElse(u.getTrades(), 0),
            u.getAddressCity(), u.getAddressState(),
            u.getCreatedAt() != null ? u.getCreatedAt().toLocalDate().toString() : null
        ));
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class PublicSellerResponse {
        private Long id;
        private String name;
        private String phone;
        private int score;
        private int trades;
        private String city;
        private String state;
        private String memberSince;
    }
}
