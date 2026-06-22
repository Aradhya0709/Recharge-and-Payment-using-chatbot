package com.paybot.controller;

import com.paybot.dto.response.ApiResponse;
import com.paybot.dto.response.TransactionResponse;
import com.paybot.model.Transaction;
import com.paybot.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // 🟢 FIX: Path badal kar '/recent' kar diya taaki frontend ki request direct match ho jaye
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionHistory(Authentication authentication) {
        String email = authentication.getName();
        List<Transaction> list = transactionService.getUserTransactionHistory(email);
        
        List<TransactionResponse> dtoList = new ArrayList<>();
        if (list != null) {
            for (Transaction t : list) {
                TransactionResponse res = new TransactionResponse();
                res.setId(t.getId());
                res.setTransactionRef(t.getTransactionRef());
                res.setType(t.getType());
                res.setStatus(t.getStatus());
                res.setAmount(t.getAmount());
                res.setServiceProvider(t.getServiceProvider());
                res.setAccountNumber(t.getAccountNumber());
                res.setDescription(t.getDescription());
                res.setBalanceBefore(t.getBalanceBefore());
                res.setBalanceAfter(t.getBalanceAfter());
                res.setCreatedAt(t.getCreatedAt());
                dtoList.add(res);
            }
        }
        return ResponseEntity.ok(ApiResponse.success("Transaction history retrieved successfully", dtoList));
    }
}