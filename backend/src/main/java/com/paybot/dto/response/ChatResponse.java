package com.paybot.dto.response;

import java.util.List;

public class ChatResponse {
    private String reply;
    private String intent;
    private List<TransactionResponse> transactions;

    public ChatResponse() {}

    public ChatResponse(String reply, String intent, List<TransactionResponse> transactions) {
        this.reply = reply;
        this.intent = intent;
        this.transactions = transactions;
    }

    public static ChatResponseBuilder builder() {
        return new ChatResponseBuilder();
    }

    // Getters and Setters
    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }

    public List<TransactionResponse> getTransactions() { return transactions; }
    public void setTransactions(List<TransactionResponse> transactions) { this.transactions = transactions; }

    public static class ChatResponseBuilder {
        private String reply;
        private String intent;
        private List<TransactionResponse> transactions;

        public ChatResponseBuilder reply(String reply) {
            this.reply = reply;
            return this;
        }

        public ChatResponseBuilder intent(String intent) {
            this.intent = intent;
            return this;
        }

        public ChatResponseBuilder transactions(List<TransactionResponse> transactions) {
            this.transactions = transactions;
            return this;
        }

        public ChatResponse build() {
            return new ChatResponse(reply, intent, transactions);
        }
    }
}