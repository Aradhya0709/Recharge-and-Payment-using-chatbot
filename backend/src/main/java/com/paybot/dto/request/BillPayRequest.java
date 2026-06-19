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
public class BillPayRequest {

    @NotNull(message = "Bill type is required")
    private TransactionType billType;

    @NotNull(message = "Service provider is required")
    private ServiceProvider serviceProvider;

    @NotBlank(message = "Account/Consumer number is required")
    @Size(min = 5, max = 50, message = "Account number must be between 5 and 50 characters")
    private String accountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum bill payment amount is ₹1.00")
    private BigDecimal amount;
}
