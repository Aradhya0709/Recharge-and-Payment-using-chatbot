package com.paybot.service;

import com.paybot.model.Transaction;
import com.paybot.model.User;
import com.paybot.model.Wallet;
import com.paybot.repository.TransactionRepository;
import com.paybot.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Wallet topUpWallet(User user, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);
        
        wallet.setBalance(balanceAfter);
        wallet = walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        String uniqueRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        transaction.setTransactionRef(uniqueRef);
        transaction.setUser(user);
        transaction.setType("WALLET_TOPUP");
        transaction.setStatus("SUCCESS");
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setDescription("Money added to wallet");

        transactionRepository.save(transaction);

        return wallet;
    }
}