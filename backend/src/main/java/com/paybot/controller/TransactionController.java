package com.paybot.controller;

import com.paybot.dto.response.ApiResponse;
import com.paybot.dto.response.TransactionResponse;
import com.paybot.model.enums.TransactionType;
import com.paybot.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactionHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String email = authentication.getName();
        Page<TransactionResponse> transactions = transactionService.getTransactionHistory(email, page, size);
        return ResponseEntity.ok(ApiResponse.success("Transaction history retrieved", transactions));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getRecentTransactions(
            Authentication authentication) {
        String email = authentication.getName();
        List<TransactionResponse> transactions = transactionService.getRecentTransactions(email);
        return ResponseEntity.ok(ApiResponse.success("Recent transactions retrieved", transactions));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByType(
            Authentication authentication,
            @PathVariable TransactionType type) {
        String email = authentication.getName();
        List<TransactionResponse> transactions = transactionService.getTransactionsByType(email, type);
        return ResponseEntity.ok(ApiResponse.success("Transactions by type retrieved", transactions));
    }

    @GetMapping("/ref/{transactionRef}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionByRef(
            Authentication authentication,
            @PathVariable String transactionRef) {
        String email = authentication.getName();
        TransactionResponse transaction = transactionService.getTransactionByRef(email, transactionRef);
        return ResponseEntity.ok(ApiResponse.success("Transaction details retrieved", transaction));
    }
}
