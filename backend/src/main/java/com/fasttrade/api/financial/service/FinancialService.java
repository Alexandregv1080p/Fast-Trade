package com.fasttrade.api.financial.service;

import com.fasttrade.api.common.dto.PageResponse;
import com.fasttrade.api.financial.entity.Transaction;
import com.fasttrade.api.financial.entity.Wallet;
import com.fasttrade.api.financial.repository.TransactionRepository;
import com.fasttrade.api.financial.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialService {

    private final TransactionRepository txRepo;
    private final WalletRepository walletRepo;

    public PageResponse<Transaction> getTransactions(int page, int pageSize) {
        var pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        var result = txRepo.findAll(pageable);
        return PageResponse.of(result.getContent(), page, pageSize, result.getTotalElements());
    }

    public PageResponse<Wallet> getWallets(int page, int pageSize) {
        var pageable = PageRequest.of(page - 1, pageSize);
        var result = walletRepo.findAll(pageable);
        return PageResponse.of(result.getContent(), page, pageSize, result.getTotalElements());
    }

    public List<Transaction> getStatement(String userId) {
        var pageable = PageRequest.of(0, 100, Sort.by("createdAt").descending());
        return txRepo.findByUserId(userId, pageable).getContent();
    }
}
