package com.fincore.dto;

public class TransactionDto {
    private String accountId;
    private String type; // DEPOSIT or WITHDRAW
    private double amount;

    public String getAccountId() {
         return accountId; 
    }
    public void setAccountId(String accountId) {
         this.accountId = accountId; 
    }
    public String getType() {
         return type; 
    }
    public void setType(String type) {
         this.type = type; 
    }
    public double getAmount() {
         return amount; 
    }
    public void setAmount(double amount) {
         this.amount = amount; 
    }
}