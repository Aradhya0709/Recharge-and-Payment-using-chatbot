package com.paybot.service;

import com.paybot.dto.request.RechargeRequest;
import com.paybot.dto.response.RechargeResponse;
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
public class RechargeService {

    private static final Set<TransactionType> VALID_RECHARGE_TYPES = Set.of(
            TransactionType.MOBILE_RECHARGE,
            TransactionType.DTH_RECHARGE,
            TransactionType.BROADBAND_RECHARGE
    );

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
    public RechargeResponse processRecharge(String email, RechargeRequest request) {
        if (!VALID_RECHARGE_TYPES.contains(request.getRechargeType())) {
            throw new InvalidOperationException("Invalid recharge type: " + request.getRechargeType()
                    + ". Valid types are: MOBILE_RECHARGE, DTH_RECHARGE, BROADBAND_RECHARGE");
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
                .type(request.getRechargeType())
                .status(TransactionStatus.SUCCESS)
                .amount(request.getAmount())
                .serviceProvider(request.getServiceProvider())
                .accountNumber(request.getAccountNumber())
                .description(buildRechargeDescription(request))
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .build();

        transactionRepository.save(transaction);

        return RechargeResponse.builder()
                .transactionRef(transactionRef)
                .status(TransactionStatus.SUCCESS.name())
                .amount(request.getAmount())
                .serviceProvider(request.getServiceProvider().name())
                .accountNumber(request.getAccountNumber())
                .rechargeType(request.getRechargeType().name())
                .walletBalance(balanceAfter)
                .message("Recharge successful for " + request.getAccountNumber())
                .build();
    }

    private String buildRechargeDescription(RechargeRequest request) {
        String typeName = request.getRechargeType().name().replace("_", " ").toLowerCase();
        return String.format("%s of ₹%s for %s via %s",
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
        return "RCH-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}
