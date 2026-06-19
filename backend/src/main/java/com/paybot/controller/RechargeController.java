package com.paybot.controller;

import com.paybot.dto.request.RechargeRequest;
import com.paybot.dto.response.ApiResponse;
import com.paybot.dto.response.RechargeResponse;
import com.paybot.service.RechargeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recharge")
public class RechargeController {

    private final RechargeService rechargeService;

    public RechargeController(RechargeService rechargeService) {
        this.rechargeService = rechargeService;
    }

    @PostMapping("/mobile")
    public ResponseEntity<ApiResponse<RechargeResponse>> mobileRecharge(
            Authentication authentication,
            @Valid @RequestBody RechargeRequest request) {
        String email = authentication.getName();
        RechargeResponse response = rechargeService.processRecharge(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Mobile recharge successful", response));
    }

    @PostMapping("/dth")
    public ResponseEntity<ApiResponse<RechargeResponse>> dthRecharge(
            Authentication authentication,
            @Valid @RequestBody RechargeRequest request) {
        String email = authentication.getName();
        RechargeResponse response = rechargeService.processRecharge(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("DTH recharge successful", response));
    }
}
