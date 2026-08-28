package com.fasttrade.api.order.service;

import com.fasttrade.api.common.dto.PageResponse;
import com.fasttrade.api.order.entity.Order;
import com.fasttrade.api.order.repository.OrderRepository;
import com.fasttrade.api.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repo;

    public PageResponse<Order> getAll(int page, int pageSize, String search, String status) {
        var pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        boolean hasSearch = search != null && !search.isBlank();
        boolean hasStatus = status != null && !status.isBlank();

        var result = (hasStatus && hasSearch)
                ? repo.findByStatusAndOrderNumberContainingIgnoreCase(status, search, pageable)
                : hasStatus
                ? repo.findByStatus(status, pageable)
                : hasSearch
                ? repo.findByOrderNumberContainingIgnoreCaseOrUserNameContainingIgnoreCase(search, search, pageable)
                : repo.findAll(pageable);
        return PageResponse.of(result.getContent(), page, pageSize, result.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Order getById(Long id) {
        Order order = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
        User user = order.getUser();
        if (user != null) {
            order.setUserEmail(user.getEmail());
            order.setUserPhone(user.getPhone());
            order.setUserCpf(user.getCpfCnpj());
            order.setAddressStreet(user.getAddressStreet());
            order.setAddressCity(user.getAddressCity());
            order.setAddressState(user.getAddressState());
            order.setAddressZip(user.getAddressZip());
        }
        return order;
    }

    public Order updateStatus(Long id, com.fasttrade.api.order.dto.StatusUpdateRequest req) {
        Order order = getById(id);
        order.setStatus(req.getStatus());
        if (req.getNote() != null) order.setStatusNote(req.getNote());
        return repo.save(order);
    }

    public PageResponse<Order> getDevolutions(int page, int pageSize) {
        var pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        var result = repo.findByStatusIn(List.of("DEVOLUTION", "REFUNDED"), pageable);
        return PageResponse.of(result.getContent(), page, pageSize, result.getTotalElements());
    }

    @Transactional(readOnly = true)
    public java.util.List<Order> getByUserId(Long userId) {
        return repo.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    public PageResponse<Order> getRefunds(int page, int pageSize) {
        var pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        var result = repo.findByStatusIn(List.of("REFUNDED"), pageable);
        return PageResponse.of(result.getContent(), page, pageSize, result.getTotalElements());
    }
}
