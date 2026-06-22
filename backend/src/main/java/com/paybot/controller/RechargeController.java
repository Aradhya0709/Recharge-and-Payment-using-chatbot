package com.paybot.controller;

import com.paybot.service.RechargeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/recharge")
@CrossOrigin(origins = "*")
public class RechargeController {

    private final RechargeService rechargeService;

    public RechargeController(RechargeService rechargeService) {
        this.rechargeService = rechargeService;
    }

    @PostMapping("/mobile")
    public ResponseEntity<?> mobileRecharge(Authentication authentication, @RequestBody Map<String, Object> payload) {
        try {
            String email = authentication.getName();
            
            // Frontend se data nikalna safely
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());
            String provider = payload.get("serviceProvider").toString();
            String account = payload.get("accountNumber").toString();

            rechargeService.processRecharge(
                    email,
                    "MOBILE_RECHARGE",
                    amount,
                    provider,
                    account
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Mobile recharge successful",
                "transactionRef", "REC-SUCCESS",
                "amount", amount,
                "accountNumber", account
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/dth")
    public ResponseEntity<?> dthRecharge(Authentication authentication, @RequestBody Map<String, Object> payload) {
        try {
            String email = authentication.getName();
            
            // Frontend se data nikalna safely
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());
            String provider = payload.get("serviceProvider").toString();
            String account = payload.get("accountNumber").toString();

            rechargeService.processRecharge(
                    email,
                    "DTH_RECHARGE",
                    amount,
                    provider,
                    account
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "DTH recharge successful",
                "transactionRef", "REC-SUCCESS",
                "amount", amount,
                "accountNumber", account
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}