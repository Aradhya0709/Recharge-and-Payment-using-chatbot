package com.paybot.service;

import com.paybot.exception.InsufficientBalanceException;
import com.paybot.exception.ResourceNotFoundException;
import com.paybot.model.Transaction;
import com.paybot.model.User;
import com.paybot.model.Wallet;
import com.paybot.repository.TransactionRepository;
import com.paybot.repository.UserRepository;
import com.paybot.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class RechargeService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public RechargeService(WalletRepository walletRepository,
                           UserRepository userRepository,
                           TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void processRecharge(String email, String type, BigDecimal amount, String provider, String account) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Wallet wallet = walletRepository.findByUserIdWithLock(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId", user.getId()));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in your wallet for this recharge.");
        }

        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(amount);
        
        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setTransactionRef("REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        transaction.setUser(user);
        transaction.setType(type != null ? type.toUpperCase() : "MOBILE_RECHARGE");
        transaction.setStatus("SUCCESS");
        transaction.setAmount(amount);
        transaction.setServiceProvider(provider);
        transaction.setAccountNumber(account);
        transaction.setDescription(provider + " recharge done for account: " + account);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);

        transactionRepository.save(transaction);
    }
}