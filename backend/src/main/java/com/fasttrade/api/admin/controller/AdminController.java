package com.fasttrade.api.admin.controller;

import com.fasttrade.api.admin.entity.AdminUser;
import com.fasttrade.api.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService service;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        return ResponseEntity.ok(service.getDashboard());
    }

    @GetMapping("/collaborators")
    public ResponseEntity<List<AdminUser>> getCollaborators() {
        return ResponseEntity.ok(service.getCollaborators());
    }

    @PostMapping("/collaborators")
    public ResponseEntity<AdminUser> createCollaborator(@RequestBody Map<String, String> data) {
        return ResponseEntity.ok(service.createCollaborator(data));
    }

    @PatchMapping("/collaborators/{id}")
    public ResponseEntity<AdminUser> updateCollaborator(@PathVariable Long id, @RequestBody Map<String, String> data) {
        return ResponseEntity.ok(service.updateCollaborator(id, data));
    }

    @DeleteMapping("/collaborators/{id}")
    public ResponseEntity<Void> deleteCollaborator(@PathVariable Long id) {
        service.deleteCollaborator(id);
        return ResponseEntity.noContent().build();
    }
}
