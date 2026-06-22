package com.paybot.service;

import com.paybot.dto.response.ChatResponse;
import com.paybot.dto.response.TransactionResponse;
import com.paybot.exception.ResourceNotFoundException;
import com.paybot.model.Transaction;
import com.paybot.model.User;
import com.paybot.model.Wallet;
import com.paybot.repository.TransactionRepository;
import com.paybot.repository.UserRepository;
import com.paybot.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public ChatbotService(UserRepository userRepository,
                          WalletRepository walletRepository,
                          TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public ChatResponse processQuery(String email, String message) {
        String lowerMessage = message.toLowerCase().trim();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (containsAny(lowerMessage, "balance", "wallet balance", "how much", "money left", "available balance", "funds")) {
            return handleBalanceQuery(user);
        }

        if (containsAny(lowerMessage, "recent transaction", "last transaction", "latest transaction",
                "recent activity", "transaction history", "show transaction", "my transaction")) {
            return handleRecentTransactions(user);
        }

        if (containsAny(lowerMessage, "recharge history", "recharge record", "mobile recharge", "dth recharge",
                "recharge detail", "past recharge", "show recharge")) {
            return handleRechargeHistory(user);
        }

        if (containsAny(lowerMessage, "bill payment", "bill history", "electricity bill", "water bill",
                "gas bill", "broadband bill", "internet bill", "utility bill", "paid bill")) {
            return handleBillPaymentHistory(user);
        }

        if (containsAny(lowerMessage, "wallet topup", "wallet top-up", "added money", "deposit",
                "topup history", "add money history")) {
            return handleWalletTopupHistory(user);
        }

        if (containsAny(lowerMessage, "total spent", "total expense", "how much spent", "expenditure")) {
            return handleTotalSpent(user);
        }

        if (containsAny(lowerMessage, "help", "what can you do", "commands", "options", "assist")) {
            return handleHelp(user);
        }

        if (containsAny(lowerMessage, "hi", "hello", "hey", "greetings", "good morning", "good evening")) {
            return ChatResponse.builder()
                    .reply("Hello " + user.getFullName() + "! 👋 How can I help you today? You can ask me about your wallet balance, transactions, recharges, or bill payments!")
                    .intent("GREETING")
                    .transactions(Collections.emptyList())
                    .build();
        }

        return ChatResponse.builder()
                .reply("I'm not sure I understand that. Here's what I can help you with:\n\n"
                        + "💰 **Balance** - Check your wallet balance\n"
                        + "📋 **Transactions** - View recent transactions\n"
                        + "📱 **Recharge History** - View past recharges\n"
                        + "🧾 **Bill Payments** - View bill payment history\n"
                        + "💵 **Wallet Top-ups** - View wallet deposit history\n"
                        + "📊 **Total Spent** - See total expenditure\n\n"
                        + "Try asking: \"What is my wallet balance?\" or \"Show my recent transactions\"")
                .intent("UNKNOWN")
                .transactions(Collections.emptyList())
                .build();
    }

    private ChatResponse handleBalanceQuery(User user) {
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId", user.getId()));

        String reply = String.format("💰 **Wallet Balance**\n\nHi %s! Your current wallet balance is **₹%s**.",
                user.getFullName(), wallet.getBalance().toString());

        return ChatResponse.builder()
                .reply(reply)
                .intent("BALANCE_CHECK")
                .transactions(Collections.emptyList())
                .build();
    }

    private ChatResponse handleRecentTransactions(User user) {
        List<Transaction> transactions = transactionRepository
                .findTop10ByUserIdOrderByCreatedAtDesc(user.getId());

        if (transactions.isEmpty()) {
            return ChatResponse.builder()
                    .reply("📋 You don't have any transactions yet. Start by adding money to your wallet!")
                    .intent("RECENT_TRANSACTIONS")
                    .transactions(Collections.emptyList())
                    .build();
        }

        StringBuilder reply = new StringBuilder("📋 **Recent Transactions** (Last " + transactions.size() + ")\n\n");
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            String sign = "WALLET_TOPUP".equalsIgnoreCase(t.getType()) ? "+" : "-";
            reply.append(String.format("%d. %s **%s₹%s** - %s (%s)\n",
                    i + 1,
                    getTypeEmoji(t.getType()),
                    sign,
                    t.getAmount(),
                    t.getDescription(),
                    t.getCreatedAt().format(DATE_FORMAT)));
        }

        return ChatResponse.builder()
                .reply(reply.toString())
                .intent("RECENT_TRANSACTIONS")
                .transactions(mapTransactions(transactions))
                .build();
    }

    private ChatResponse handleRechargeHistory(User user) {
        List<Transaction> mobileTxns = transactionRepository
                .findByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), "MOBILE_RECHARGE");
        List<Transaction> dthTxns = transactionRepository
                .findByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), "DTH_RECHARGE");
        
        List<Transaction> transactions = new ArrayList<>();
        if (mobileTxns != null) transactions.addAll(mobileTxns);
        if (dthTxns != null) transactions.addAll(dthTxns);

        if (transactions.isEmpty()) {
            return ChatResponse.builder()
                    .reply("📱 No recharge history found. You haven't done any recharges yet.")
                    .intent("RECHARGE_HISTORY")
                    .transactions(Collections.emptyList())
                    .build();
        }

        transactions.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        List<Transaction> recent = transactions.size() > 10 ? transactions.subList(0, 10) : transactions;

        StringBuilder reply = new StringBuilder("📱 **Recharge History** (Last " + recent.size() + ")\n\n");
        for (int i = 0; i < recent.size(); i++) {
            Transaction t = recent.get(i);
            String typeName = t.getType() != null ? t.getType().replace("_", " ") : "RECHARGE";
            reply.append(String.format("%d. %s **₹%s** - %s → %s (%s)\n",
                    i + 1,
                    getTypeEmoji(t.getType()),
                    t.getAmount(),
                    typeName,
                    t.getAccountNumber(),
                    t.getCreatedAt().format(DATE_FORMAT)));
        }

        return ChatResponse.builder()
                .reply(reply.toString())
                .intent("RECHARGE_HISTORY")
                .transactions(mapTransactions(recent))
                .build();
    }

    private ChatResponse handleBillPaymentHistory(User user) {
        List<Transaction> allBills = new ArrayList<>();
        for (String type : List.of("ELECTRICITY_BILL", "WATER_BILL", "GAS_BILL", "INTERNET_BILL")) {
            List<Transaction> bills = transactionRepository.findByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), type);
            if (bills != null) {
                allBills.addAll(bills);
            }
        }

        if (allBills.isEmpty()) {
            return ChatResponse.builder()
                    .reply("🧾 No bill payment history found. You haven't paid any bills yet.")
                    .intent("BILL_HISTORY")
                    .transactions(Collections.emptyList())
                    .build();
        }

        allBills.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        List<Transaction> recent = allBills.size() > 10 ? allBills.subList(0, 10) : allBills;

        StringBuilder reply = new StringBuilder("🧾 **Bill Payment History** (Last " + recent.size() + ")\n\n");
        for (int i = 0; i < recent.size(); i++) {
            Transaction t = recent.get(i);
            String typeName = t.getType() != null ? t.getType().replace("_", " ") : "BILL";
            reply.append(String.format("%d. %s **₹%s** - %s for account %s (%s)\n",
                    i + 1,
                    getTypeEmoji(t.getType()),
                    t.getAmount(),
                    typeName,
                    t.getAccountNumber(),
                    t.getCreatedAt().format(DATE_FORMAT)));
        }

        return ChatResponse.builder()
                .reply(reply.toString())
                .intent("BILL_HISTORY")
                .transactions(mapTransactions(recent))
                .build();
    }

    private ChatResponse handleWalletTopupHistory(User user) {
        List<Transaction> topups = transactionRepository
                .findByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), "WALLET_TOPUP");

        if (topups.isEmpty()) {
            return ChatResponse.builder()
                    .reply("💵 No wallet top-up history found. Add money to your wallet to get started!")
                    .intent("TOPUP_HISTORY")
                    .transactions(Collections.emptyList())
                    .build();
        }

        List<Transaction> recent = topups.size() > 10 ? topups.subList(0, 10) : topups;
        BigDecimal total = topups.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        StringBuilder reply = new StringBuilder("💵 **Wallet Top-up History**\n\n");
        reply.append(String.format("Total deposited: **₹%s** across %d top-ups\n\n", total, topups.size()));
        for (int i = 0; i < recent.size(); i++) {
            Transaction t = recent.get(i);
            reply.append(String.format("%d. +**₹%s** (%s)\n",
                    i + 1, t.getAmount(), t.getCreatedAt().format(DATE_FORMAT)));
        }

        return ChatResponse.builder()
                .reply(reply.toString())
                .intent("TOPUP_HISTORY")
                .transactions(mapTransactions(recent))
                .build();
    }

    private ChatResponse handleTotalSpent(User user) {
        BigDecimal totalSpent = BigDecimal.ZERO;
        BigDecimal totalDeposited = BigDecimal.ZERO;
        int debitCount = 0;
        int creditCount = 0;

        List<Transaction> all = transactionRepository.findByUserIdAndTypeOrderByCreatedAtDesc(
                user.getId(), "WALLET_TOPUP");
        if (all != null) {
            for (Transaction t : all) {
                totalDeposited = totalDeposited.add(t.getAmount());
                creditCount++;
            }
        }

        for (String type : List.of("MOBILE_RECHARGE", "DTH_RECHARGE", "ELECTRICITY_BILL", 
                "WATER_BILL", "GAS_BILL", "INTERNET_BILL", "BROADBAND_RECHARGE")) {
            List<Transaction> byType = transactionRepository.findByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), type);
            if (byType != null) {
                for (Transaction t : byType) {
                    totalSpent = totalSpent.add(t.getAmount());
                    debitCount++;
                }
            }
        }

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId", user.getId()));

        String reply = String.format(
                "📊 **Financial Summary**\n\n"
                        + "💰 Current Balance: **₹%s**\n"
                        + "💵 Total Deposited: **₹%s** (%d transactions)\n"
                        + "💸 Total Spent: **₹%s** (%d transactions)\n",
                wallet.getBalance(), totalDeposited, creditCount, totalSpent, debitCount);

        return ChatResponse.builder()
                .reply(reply)
                .intent("TOTAL_SPENT")
                .transactions(Collections.emptyList())
                .build();
    }

    private ChatResponse handleHelp(User user) {
        String reply = "🤖 **PayBot Assistant - Help**\n\n"
                + "Here's what I can do for you, " + user.getFullName() + ":\n\n"
                + "💰 **\"What is my balance?\"** - Check wallet balance\n"
                + "📋 **\"Show recent transactions\"** - View last 10 transactions\n"
                + "📱 **\"Show recharge history\"** - View mobile & DTH recharges\n"
                + "🧾 **\"Show bill payments\"** - View bill payment history\n"
                + "💵 **\"Show wallet top-ups\"** - View wallet deposit history\n"
                + "📊 **\"How much have I spent?\"** - View total expenditure\n\n"
                + "Just type your question naturally and I'll help! 😊";

        return ChatResponse.builder()
                .reply(reply)
                .intent("HELP")
                .transactions(Collections.emptyList())
                .build();
    }

    private boolean containsAny(String message, String... keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword)) return true;
        }
        return false;
    }

    private String getTypeEmoji(String type) {
        if (type == null) return "💸";
        return switch (type.toUpperCase()) {
            case "WALLET_TOPUP" -> "💰";
            case "MOBILE_RECHARGE" -> "📱";
            case "DTH_RECHARGE" -> "📺";
            case "ELECTRICITY_BILL" -> "⚡";
            case "WATER_BILL" -> "💧";
            case "GAS_BILL" -> "🔥";
            case "INTERNET_BILL", "BROADBAND_RECHARGE" -> "🌐";
            default -> "💸";
        };
    }

    private List<TransactionResponse> mapTransactions(List<Transaction> transactions) {
        if (transactions == null) return Collections.emptyList();
        
        return transactions.stream()
                .map(t -> {
                    TransactionResponse res = new TransactionResponse();
                    res.setId(t.getId());
                    res.setTransactionRef(t.getTransactionRef());
                    res.setType(t.getType());
                    res.setStatus(t.getStatus());
                    res.setAmount(t.getAmount());
                    res.setServiceProvider(t.getServiceProvider());
                    res.setAccountNumber(t.getAccountNumber());
                    res.setDescription(t.getDescription());
                    res.setBalanceBefore(t.getBalanceBefore());
                    res.setBalanceAfter(t.getBalanceAfter());
                    res.setCreatedAt(t.getCreatedAt());
                    return res;
                })
                .collect(Collectors.toList());
    }
}