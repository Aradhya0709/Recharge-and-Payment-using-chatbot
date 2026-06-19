package com.paybot.controller;

import com.paybot.dto.request.BillPayRequest;
import com.paybot.dto.response.ApiResponse;
import com.paybot.dto.response.BillPayResponse;
import com.paybot.service.BillPayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bills")
public class BillPayController {

    private final BillPayService billPayService;

    public BillPayController(BillPayService billPayService) {
        this.billPayService = billPayService;
    }

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<BillPayResponse>> payBill(
            Authentication authentication,
            @Valid @RequestBody BillPayRequest request) {
        String email = authentication.getName();
        BillPayResponse response = billPayService.processBillPayment(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bill payment successful", response));
    }
}
