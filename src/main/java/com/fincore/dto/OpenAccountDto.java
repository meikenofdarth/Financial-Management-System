package com.fincore.dto;

public class OpenAccountDto {
    private String customerId;
    private String type; // SAVINGS or CURRENT
    private double initialDeposit;

    public String getCustomerId() {
         return customerId; 
    }
    public void setCustomerId(String customerId) {
         this.customerId = customerId; 
    }
    public String getType() {
         return type; 
    }
    public void setType(String type) {
         this.type = type; 
    }
    public double getInitialDeposit() {
         return initialDeposit; 
    }
    public void setInitialDeposit(double initialDeposit) {
         this.initialDeposit = initialDeposit; 
    }
}