package com.fasttrade.api.auth.service;

import com.fasttrade.api.admin.entity.AdminUser;
import com.fasttrade.api.admin.repository.AdminUserRepository;
import com.fasttrade.api.auth.dto.CustomerLoginRequest;
import com.fasttrade.api.auth.dto.LoginRequest;
import com.fasttrade.api.auth.dto.LoginResponse;
import com.fasttrade.api.security.JwtUtil;
import com.fasttrade.api.user.entity.User;
import com.fasttrade.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminUserRepository adminRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse loginCustomer(CustomerLoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Credenciais inválidas"));

        if (Boolean.TRUE.equals(user.getBlocked())) {
            throw new ResponseStatusException(FORBIDDEN, "Conta bloqueada. Entre em contato com o suporte.");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Credenciais inválidas");
        }

        String token = jwtUtil.generate(user.getEmail(), "CUSTOMER");
        return new LoginResponse(token, user.getId(), user.getName(), user.getEmail(), "CUSTOMER");
    }

    public LoginResponse login(LoginRequest req) {
        AdminUser admin = adminRepo.findByEmail(req.getCredential())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Credenciais inválidas"));

        if (!passwordEncoder.matches(req.getPassword(), admin.getPassword())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Credenciais inválidas");
        }

        String token = jwtUtil.generate(admin.getEmail(), admin.getRole());
        return new LoginResponse(token, admin.getId(), admin.getName(), admin.getEmail(), admin.getRole());
    }

    public void sendRecovery(String email) {
        // Placeholder: em produção, enviar e-mail com código
        // Por ora só verifica se o usuário existe
        adminRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "E-mail não encontrado"));

        // TODO: gerar código, salvar no banco, enviar por e-mail
        System.out.println("[DEV] Código de recuperação para " + email + ": 123456");
    }

    public void resetPassword(String email, String code, String newPassword) {
        // Placeholder: em produção, validar código
        AdminUser admin = adminRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "E-mail não encontrado"));

        // TODO: validar código antes de trocar a senha
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepo.save(admin);
    }
}
