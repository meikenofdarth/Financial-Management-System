package com.fincore.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single financial transaction in the system.
 */
public class Transaction implements Serializable{
    private static final long serialVersionUID = 1L;
    private String transactionId;
    private String accountId;
    private String type; // DEPOSIT, WITHDRAWAL, TRANSFER, LOAN_DISBURSAL, INTEREST
    private double amount;
    private LocalDateTime timestamp;
    private String description;

    public Transaction(String transactionId, String accountId, String type, double amount, String description) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.description = description;
    }

    // Getters
    public String getTransactionId() {
         return transactionId; 
    }
    public String getAccountId() {
         return accountId; 
    }
    public String getType() {
         return type; 
    }
    public double getAmount() {
         return amount; 
    }
    public LocalDateTime getTimestamp() {
         return timestamp; 
    }
    public String getDescription() {
         return description; 
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s | Type: %s | Amount: $%.2f | Note: %s",
                timestamp.format(formatter), transactionId, type, amount, description);
    }
}