package com.fasttrade.api.auth.controller;

import com.fasttrade.api.auth.dto.*;
import com.fasttrade.api.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── Clientes (app Android) ──────────────────────────────────────────────
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> loginCustomer(@Valid @RequestBody CustomerLoginRequest req) {
        return ResponseEntity.ok(authService.loginCustomer(req));
    }

    @PostMapping("/auth/recovery")
    public ResponseEntity<Map<String, String>> customerRecovery(@RequestBody RecoveryRequest req) {
        authService.sendRecovery(req.getEmail());
        return ResponseEntity.ok(Map.of("message", "Código de recuperação enviado para " + req.getEmail()));
    }

    // ── Admin (painel web) ──────────────────────────────────────────────────
    @PostMapping("/auth/login/adm")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/admin/recovery")
    public ResponseEntity<Map<String, String>> recovery(@RequestBody RecoveryRequest req) {
        authService.sendRecovery(req.getEmail());
        return ResponseEntity.ok(Map.of("message", "Código de recuperação enviado para " + req.getEmail()));
    }

    @PostMapping("/admin/reset")
    public ResponseEntity<Map<String, String>> reset(@RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req.getEmail(), req.getCode(), req.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso."));
    }
}
