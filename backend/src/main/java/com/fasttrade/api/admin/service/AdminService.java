package com.fasttrade.api.admin.service;

import com.fasttrade.api.admin.entity.AdminUser;
import com.fasttrade.api.admin.repository.AdminUserRepository;
import com.fasttrade.api.order.repository.OrderRepository;
import com.fasttrade.api.product.repository.ProductRepository;
import com.fasttrade.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminUserRepository adminRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> getDashboard() {
        long totalUsers    = userRepo.count();
        long totalProducts = productRepo.count();
        long totalOrders   = orderRepo.count();

        var recentOrders = orderRepo.findAll(
                PageRequest.of(0, 5, Sort.by("createdAt").descending())
        ).getContent();

        // Build ordersByStatus map: { "PENDING": 12, "DELIVERED": 34, ... }
        Map<String, Long> ordersByStatus = new HashMap<>();
        for (Object[] row : orderRepo.countByStatus()) {
            ordersByStatus.put((String) row[0], (Long) row[1]);
        }

        Map<String, Object> dash = new HashMap<>();
        dash.put("users", totalUsers);
        dash.put("products", totalProducts);
        dash.put("orders", totalOrders);
        dash.put("usersTrend", "+0%");
        dash.put("recentOrders", recentOrders);
        dash.put("ordersByStatus", ordersByStatus);
        return dash;
    }

    public List<AdminUser> getCollaborators() {
        return adminRepo.findAll();
    }

    public AdminUser createCollaborator(com.fasttrade.api.admin.dto.CollaboratorRequest data) {
        if (adminRepo.existsByEmail(data.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        var admin = new AdminUser();
        admin.setName(data.getName());
        admin.setEmail(data.getEmail());
        admin.setPassword(passwordEncoder.encode(data.getPassword()));
        admin.setRole(data.getRole() != null ? data.getRole() : "ADMIN");
        return adminRepo.save(admin);
    }

    public AdminUser updateCollaborator(Long id, com.fasttrade.api.admin.dto.CollaboratorRequest data) {
        AdminUser admin = adminRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (data.getName() != null) admin.setName(data.getName());
        if (data.getRole() != null) admin.setRole(data.getRole());
        if (data.getPassword() != null && !data.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode(data.getPassword()));
        }
        return adminRepo.save(admin);
    }

    public void deleteCollaborator(Long id) {
        if (!adminRepo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        adminRepo.deleteById(id);
    }
}
