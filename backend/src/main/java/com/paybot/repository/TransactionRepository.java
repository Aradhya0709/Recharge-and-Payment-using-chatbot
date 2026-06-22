package com.paybot.repository;

import com.paybot.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // 🟢 Chatbot aur baki controller service codes ke liye required parameters list:
    List<Transaction> findByUserId(Long userId);
    
    List<Transaction> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Transaction> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type);
}