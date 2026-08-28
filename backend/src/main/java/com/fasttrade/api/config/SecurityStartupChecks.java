package com.fasttrade.api.config;

import com.fasttrade.api.admin.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Endurecimento no boot (Fase 5.6), tudo não-destrutivo:
 *  - avisa se o JWT ainda usa o secret de desenvolvimento;
 *  - se ADMIN_PASSWORD estiver definido, redefine a senha do admin (produção);
 *    sem ele, mantém o seed e só avisa. Nada quebra o dev.
 */
@Component
@RequiredArgsConstructor
public class SecurityStartupChecks implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SecurityStartupChecks.class);
    private static final String DEV_JWT_SECRET =
            "4f6e8b2c9d1a7e3f5g2h8i4j6k0l2m9n1o3p5q7r2s4t6u8v0w2x4y6z8a1b3c5";

    private final AdminUserRepository adminRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.secret}")
    private String jwtSecret;
    @Value("${app.admin.email:admin@fasttrade.com}")
    private String adminEmail;
    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (DEV_JWT_SECRET.equals(jwtSecret)) {
            log.warn("⚠️  JWT usando o secret de DESENVOLVIMENTO — defina JWT_SECRET em produção.");
        }
        if (adminPassword == null || adminPassword.isBlank()) {
            log.warn("⚠️  ADMIN_PASSWORD não definido — admin com a senha padrão do seed; troque em produção.");
            return;
        }
        adminRepo.findByEmail(adminEmail).ifPresent(admin -> {
            admin.setPassword(passwordEncoder.encode(adminPassword));
            adminRepo.save(admin);
            log.info("Senha do admin {} redefinida a partir de ADMIN_PASSWORD.", adminEmail);
        });
    }
}
