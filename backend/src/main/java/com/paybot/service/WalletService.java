package com.paybot.service;

import com.paybot.dto.request.WalletTopUpRequest;
import com.paybot.dto.response.TransactionResponse;
import com.paybot.dto.response.WalletResponse;
import com.paybot.exception.ResourceNotFoundException;
import com.paybot.model.Transaction;
import com.paybot.model.User;
import com.paybot.model.Wallet;
import com.paybot.model.enums.TransactionStatus;
import com.paybot.model.enums.TransactionType;
import com.paybot.repository.TransactionRepository;
import com.paybot.repository.UserRepository;
import com.paybot.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository,
                         UserRepository userRepository,
                         TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public WalletResponse getWalletByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId", user.getId()));

        return mapToWalletResponse(wallet, user);
    }

    @Transactional
    public TransactionResponse topUpWallet(String email, WalletTopUpRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Wallet wallet = walletRepository.findByUserIdWithLock(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId", user.getId()));

        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(request.getAmount());

        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .transactionRef(generateTransactionRef())
                .user(user)
                .type(TransactionType.WALLET_TOPUP)
                .status(TransactionStatus.SUCCESS)
                .amount(request.getAmount())
                .description("Wallet top-up of ₹" + request.getAmount())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .build();

        transaction = transactionRepository.save(transaction);

        return mapToTransactionResponse(transaction);
    }

    public BigDecimal getBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId", user.getId()));

        return wallet.getBalance();
    }

    private WalletResponse mapToWalletResponse(Wallet wallet, User user) {
        return WalletResponse.builder()
                .walletId(wallet.getId())
                .balance(wallet.getBalance())
                .ownerName(user.getFullName())
                .ownerEmail(user.getEmail())
                .lastUpdated(wallet.getUpdatedAt())
                .build();
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionRef(transaction.getTransactionRef())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .amount(transaction.getAmount())
                .serviceProvider(transaction.getServiceProvider())
                .accountNumber(transaction.getAccountNumber())
                .description(transaction.getDescription())
                .balanceBefore(transaction.getBalanceBefore())
                .balanceAfter(transaction.getBalanceAfter())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    private String generateTransactionRef() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}
