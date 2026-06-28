package com.fasttrade.api.financial.controller;

import com.fasttrade.api.common.dto.PageResponse;
import com.fasttrade.api.financial.entity.Transaction;
import com.fasttrade.api.financial.entity.Wallet;
import com.fasttrade.api.financial.service.FinancialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/financial")
@RequiredArgsConstructor
public class FinancialController {

    private final FinancialService service;

    @GetMapping("/transactions")
    public ResponseEntity<PageResponse<Transaction>> getTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(service.getTransactions(page, pageSize));
    }

    @GetMapping("/wallets")
    public ResponseEntity<PageResponse<Wallet>> getWallets(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(service.getWallets(page, pageSize));
    }

    @GetMapping("/statement/{userId}")
    public ResponseEntity<List<Transaction>> getStatement(@PathVariable String userId) {
        return ResponseEntity.ok(service.getStatement(userId));
    }
}
