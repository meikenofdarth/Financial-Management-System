package com.fincore.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    protected String accountId;
    protected String customerId;
    protected double balance;
    protected boolean isActive;
    protected List<Transaction> transactionHistory;

    public Account(String accountId, String customerId, double openingBalance) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.balance = openingBalance;
        this.isActive = true;
        this.transactionHistory = new ArrayList<>();
        // Record opening transaction
        addTransaction(new Transaction("INIT-" + System.currentTimeMillis(), accountId, "OPENING", openingBalance, "Account Opened"));
    }

    public String getAccountId() { return accountId; }
    public String getCustomerId() { return customerId; }
    public double getBalance() { return balance; }
    public boolean isActive() { return isActive; }

    public void closeAccount() {
        this.isActive = false;
    }

    public void addTransaction(Transaction t) {
        this.transactionHistory.add(t);
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    // Abstract methods that subclasses must implement
    // This forces specific logic in subclasses, adding to complexity
    public abstract boolean canWithdraw(double amount);
    public abstract void applyEndOfMonthBenefits();
    public abstract String getAccountType();
    public void deposit(double amount) {
        this.balance += amount;
    }
    public void withdraw(double amount) {
        this.balance -= amount;
    }
}