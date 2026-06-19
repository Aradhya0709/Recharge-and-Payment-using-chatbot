package com.paybot.dto.response;

import com.paybot.model.enums.ServiceProvider;
import com.paybot.model.enums.TransactionStatus;
import com.paybot.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String transactionRef;
    private TransactionType type;
    private TransactionStatus status;
    private BigDecimal amount;
    private ServiceProvider serviceProvider;
    private String accountNumber;
    private String description;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private LocalDateTime createdAt;
}
