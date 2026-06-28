package com.fasttrade.api.financial.repository;

import com.fasttrade.api.financial.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findAll(Pageable pageable);
    Page<Transaction> findByUserId(String userId, Pageable pageable);
}
