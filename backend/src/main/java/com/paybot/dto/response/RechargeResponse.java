package com.paybot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargeResponse {
    private String transactionRef;
    private String status;
    private BigDecimal amount;
    private String serviceProvider;
    private String accountNumber;
    private String rechargeType;
    private BigDecimal walletBalance;
    private String message;
}
