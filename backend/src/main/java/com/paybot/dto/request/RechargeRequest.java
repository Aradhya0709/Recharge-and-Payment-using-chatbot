package com.paybot.dto.request;

import com.paybot.model.enums.ServiceProvider;
import com.paybot.model.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargeRequest {

    @NotNull(message = "Recharge type is required")
    private TransactionType rechargeType;

    @NotNull(message = "Service provider is required")
    private ServiceProvider serviceProvider;

    @NotBlank(message = "Mobile/Account number is required")
    @Size(min = 10, max = 50, message = "Number must be between 10 and 50 characters")
    private String accountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "10.00", message = "Minimum recharge amount is ₹10.00")
    private BigDecimal amount;
}
