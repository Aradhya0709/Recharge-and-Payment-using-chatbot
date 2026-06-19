package com.paybot.model;

import com.paybot.model.enums.ServiceProvider;
import com.paybot.model.enums.TransactionStatus;
import com.paybot.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String transactionRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private ServiceProvider serviceProvider;

    @Column(length = 50)
    private String accountNumber;

    @Column(length = 255)
    private String description;

    @Column(precision = 12, scale = 2)
    private BigDecimal balanceBefore;

    @Column(precision = 12, scale = 2)
    private BigDecimal balanceAfter;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
