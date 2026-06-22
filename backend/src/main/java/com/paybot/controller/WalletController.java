package com.paybot.controller;

import com.paybot.dto.request.WalletTopUpRequest;
import com.paybot.dto.response.ApiResponse;
import com.paybot.model.User;
import com.paybot.model.Wallet;
import com.paybot.repository.UserRepository;
import com.paybot.repository.WalletRepository;
import com.paybot.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository; // 🟢 Direct repo added to fix service error

    public WalletController(WalletService walletService, 
                            UserRepository userRepository, 
                            WalletRepository walletRepository) {
        this.walletService = walletService;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    // 🟢 FIX: Service dependecy hata kar direct repository se live balance secure fetch kiya bina logic chhede
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getBalance(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // User ID ke base par direct safe database wallet retrieval
            Wallet wallet = walletRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Wallet not found"));
            
            return ResponseEntity.ok(ApiResponse.success(
                    "Balance retrieved successfully",
                    Map.of("balance", wallet.getBalance())
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 🟢 AAPKA ORIGINAL ADD MONEY LOGIC (No change)
    @PostMapping("/add-money")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addMoney(
            Authentication authentication,
            @Valid @RequestBody WalletTopUpRequest request) {
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet updatedWallet = walletService.topUpWallet(user, request.getAmount());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Money added to wallet successfully", Map.of(
                        "status", "SUCCESS",
                        "balance", updatedWallet.getBalance()
                )));
    }
}