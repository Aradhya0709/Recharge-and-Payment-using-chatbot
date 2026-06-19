package com.paybot.controller;

import com.paybot.dto.request.ChatRequest;
import com.paybot.dto.response.ApiResponse;
import com.paybot.dto.response.ChatResponse;
import com.paybot.service.ChatbotService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/query")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            Authentication authentication,
            @Valid @RequestBody ChatRequest request) {
        String email = authentication.getName();
        ChatResponse response = chatbotService.processQuery(email, request.getMessage());
        return ResponseEntity.ok(ApiResponse.success("Query processed successfully", response));
    }
}
