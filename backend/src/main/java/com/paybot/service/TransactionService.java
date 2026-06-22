package com.paybot.service;

import com.paybot.model.Transaction;
import com.paybot.model.User;
import com.paybot.repository.TransactionRepository;
import com.paybot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> getUserTransactionHistory(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            // 🟢 FIX: Direct findByUserId ki jagah OrderByCreatedAtDesc use kiya taaki latest transactions pehle dikhein
            return transactionRepository.findTop10ByUserIdOrderByCreatedAtDesc(userOpt.get().getId());
        }
        
        return Collections.emptyList();
    }
}