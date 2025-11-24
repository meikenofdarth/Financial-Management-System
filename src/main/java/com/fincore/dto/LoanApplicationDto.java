package com.fincore.dto;

public class LoanApplicationDto {
    private String customerId;
    private double amount;
    private int tenureMonths;

    public String getCustomerId() {
         return customerId; 
    }
    public void setCustomerId(String customerId) { 
        this.customerId = customerId; 
    }
    public double getAmount() {
         return amount; 
    }
    public void setAmount(double amount) {
         this.amount = amount; 
    }
    public int getTenureMonths() {
         return tenureMonths; 
    }
    public void setTenureMonths(int tenureMonths) {
         this.tenureMonths = tenureMonths; 
    }
}