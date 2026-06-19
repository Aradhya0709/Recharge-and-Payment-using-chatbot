package com.paybot.controller;

import com.paybot.dto.request.WalletTopUpRequest;
import com.paybot.dto.response.ApiResponse;
import com.paybot.dto.response.TransactionResponse;
import com.paybot.dto.response.WalletResponse;
import com.paybot.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getBalance(Authentication authentication) {
        String email = authentication.getName();
        BigDecimal balance = walletService.getBalance(email);
        return ResponseEntity.ok(ApiResponse.success(
                "Balance retrieved successfully",
                Map.of("balance", balance)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(Authentication authentication) {
        String email = authentication.getName();
        WalletResponse response = walletService.getWalletByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Wallet details retrieved", response));
    }

    @PostMapping("/add-money")
    public ResponseEntity<ApiResponse<TransactionResponse>> addMoney(
            Authentication authentication,
            @Valid @RequestBody WalletTopUpRequest request) {
        String email = authentication.getName();
        TransactionResponse response = walletService.topUpWallet(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Money added to wallet successfully", response));
    }
}
