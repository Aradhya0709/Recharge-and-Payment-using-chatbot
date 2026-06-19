package com.paybot.repository;

import com.paybot.model.Transaction;
import com.paybot.model.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Transaction> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    List<Transaction> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, TransactionType type);

    List<Transaction> findByUserIdAndTypeInOrderByCreatedAtDesc(Long userId, List<TransactionType> types);

    Optional<Transaction> findByTransactionRef(String transactionRef);

    List<Transaction> findTop5ByUserIdAndTypeInOrderByCreatedAtDesc(Long userId, List<TransactionType> types);

    List<Transaction> findTop5ByUserIdAndTypeOrderByCreatedAtDesc(Long userId, TransactionType type);
}
