package com.paybot.service;

import com.paybot.dto.request.BillPayRequest;
import com.paybot.dto.response.BillPayResponse;
import com.paybot.exception.InsufficientBalanceException;
import com.paybot.exception.InvalidOperationException;
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
import java.util.Set;
import java.util.UUID;

@Service
public class BillPayService {

    private static final Set<String> VALID_BILL_TYPES = Set.of(
            "ELECTRICITY_BILL",
            "WATER_BILL",
            "GAS_BILL",
            "INTERNET_BILL"
    );

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public BillPayService(WalletRepository walletRepository,
                          UserRepository userRepository,
                          TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public BillPayResponse processBillPayment(String email, BillPayRequest request) {
        String billTypeInput = request.getBillType() != null ? request.getBillType().toString() : "";
        
        if (!VALID_BILL_TYPES.contains(billTypeInput)) {
            throw new InvalidOperationException("Invalid bill type: " + billTypeInput
                    + ". Valid types are: ELECTRICITY_BILL, WATER_BILL, GAS_BILL, INTERNET_BILL");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Wallet wallet = walletRepository.findByUserIdWithLock(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId", user.getId()));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    String.format("Insufficient balance. Available: ₹%s, Required: ₹%s",
                            wallet.getBalance(), request.getAmount()));
        }

        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(request.getAmount());
        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);

        String transactionRef = generateTransactionRef();

        // 🟢 FIX: String types direct map mapping without builder entity crash
        Transaction transaction = new Transaction();
        transaction.setTransactionRef(transactionRef);
        transaction.setUser(user);
        transaction.setType(billTypeInput);
        transaction.setStatus("SUCCESS");
        transaction.setAmount(request.getAmount());
        transaction.setServiceProvider(request.getServiceProvider() != null ? request.getServiceProvider().toString() : "UNKNOWN");
        transaction.setAccountNumber(request.getAccountNumber());
        transaction.setDescription(buildBillDescription(request));
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);

        transactionRepository.save(transaction);

        String providerName = request.getServiceProvider() != null ? request.getServiceProvider().toString() : "UNKNOWN";

        return BillPayResponse.builder()
                .transactionRef(transactionRef)
                .status("SUCCESS")
                .amount(request.getAmount())
                .serviceProvider(providerName)
                .accountNumber(request.getAccountNumber())
                .billType(billTypeInput)
                .walletBalance(balanceAfter)
                .message("Bill payment successful for account " + request.getAccountNumber())
                .build();
    }

    private String buildBillDescription(BillPayRequest request) {
        String typeInput = request.getBillType() != null ? request.getBillType().toString() : "BILL";
        String typeName = typeInput.replace("_", " ").toLowerCase();
        String providerName = request.getServiceProvider() != null ? request.getServiceProvider().toString().replace("_", " ") : "UNKNOWN";
        return String.format("%s payment of ₹%s for account %s via %s",
                capitalize(typeName),
                request.getAmount(),
                request.getAccountNumber(),
                providerName);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String generateTransactionRef() {
        return "BIL-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}