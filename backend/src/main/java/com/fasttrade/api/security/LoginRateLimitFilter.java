package com.fasttrade.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limit simples de força bruta: no máximo {@value #MAX} POSTs a /api/auth/ por IP
 * numa janela de {@value #WINDOW_MS} ms. Fail-open — só barra quando estoura o limite.
 *
 * ponytail: contador em memória por instância (e o mapa não é limpo). Suficiente pra um
 * pod; para multi-instância trocar por Redis/bucket4j.
 */
@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX = 10;
    private static final long WINDOW_MS = 60_000;

    // ip -> [inicioDaJanelaMs, contagem]
    private final Map<String, long[]> hits = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        if ("POST".equalsIgnoreCase(req.getMethod()) && req.getRequestURI().startsWith("/api/auth/")
                && isOverLimit(clientIp(req))) {
            res.setStatus(429);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"error\":\"Muitas tentativas. Tente novamente em instantes.\"}");
            return;
        }
        chain.doFilter(req, res);
    }

    private boolean isOverLimit(String ip) {
        long now = System.currentTimeMillis();
        long[] w = hits.compute(ip, (k, cur) -> {
            if (cur == null || now - cur[0] > WINDOW_MS) return new long[]{now, 1};
            cur[1]++;
            return cur;
        });
        return w[1] > MAX;
    }

    private String clientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : req.getRemoteAddr();
    }
}
