package com.paybot.service;

import com.paybot.dto.response.TransactionResponse;
import com.paybot.exception.ResourceNotFoundException;
import com.paybot.model.Transaction;
import com.paybot.model.User;
import com.paybot.model.enums.TransactionType;
import com.paybot.repository.TransactionRepository;
import com.paybot.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Page<TransactionResponse> getTransactionHistory(String email, int page, int size) {
        User user = getUserByEmail(email);
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        return transactions.map(this::mapToTransactionResponse);
    }

    public List<TransactionResponse> getRecentTransactions(String email) {
        User user = getUserByEmail(email);
        List<Transaction> transactions = transactionRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId());
        return transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByType(String email, TransactionType type) {
        User user = getUserByEmail(email);
        List<Transaction> transactions = transactionRepository
                .findByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), type);
        return transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse getTransactionByRef(String email, String transactionRef) {
        User user = getUserByEmail(email);
        Transaction transaction = transactionRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "ref", transactionRef));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Transaction", "ref", transactionRef);
        }

        return mapToTransactionResponse(transaction);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
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
}
