package com.fasttrade.api.order.repository;

import com.fasttrade.api.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatus();

    Page<Order> findByStatus(String status, Pageable pageable);

    Page<Order> findByOrderNumberContainingIgnoreCaseOrUserNameContainingIgnoreCase(
            String orderNumber, String userName, Pageable pageable);

    Page<Order> findByStatusAndOrderNumberContainingIgnoreCase(String status, String search, Pageable pageable);

    Page<Order> findByStatusIn(java.util.List<String> statuses, Pageable pageable);

    java.util.List<Order> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
