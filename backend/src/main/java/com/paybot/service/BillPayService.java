package com.paybot.service;

import com.paybot.dto.request.BillPayRequest;
import com.paybot.dto.response.BillPayResponse;
import com.paybot.exception.InsufficientBalanceException;
import com.paybot.exception.InvalidOperationException;
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
import java.util.Set;
import java.util.UUID;

@Service
public class BillPayService {

    private static final Set<TransactionType> VALID_BILL_TYPES = Set.of(
            TransactionType.ELECTRICITY_BILL,
            TransactionType.WATER_BILL,
            TransactionType.GAS_BILL,
            TransactionType.INTERNET_BILL
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
        if (!VALID_BILL_TYPES.contains(request.getBillType())) {
            throw new InvalidOperationException("Invalid bill type: " + request.getBillType()
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

        Transaction transaction = Transaction.builder()
                .transactionRef(transactionRef)
                .user(user)
                .type(request.getBillType())
                .status(TransactionStatus.SUCCESS)
                .amount(request.getAmount())
                .serviceProvider(request.getServiceProvider())
                .accountNumber(request.getAccountNumber())
                .description(buildBillDescription(request))
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .build();

        transactionRepository.save(transaction);

        return BillPayResponse.builder()
                .transactionRef(transactionRef)
                .status(TransactionStatus.SUCCESS.name())
                .amount(request.getAmount())
                .serviceProvider(request.getServiceProvider().name())
                .accountNumber(request.getAccountNumber())
                .billType(request.getBillType().name())
                .walletBalance(balanceAfter)
                .message("Bill payment successful for account " + request.getAccountNumber())
                .build();
    }

    private String buildBillDescription(BillPayRequest request) {
        String typeName = request.getBillType().name().replace("_", " ").toLowerCase();
        return String.format("%s payment of ₹%s for account %s via %s",
                capitalize(typeName),
                request.getAmount(),
                request.getAccountNumber(),
                request.getServiceProvider().name().replace("_", " "));
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String generateTransactionRef() {
        return "BIL-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}
