package com.fincore.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Loan implements Serializable{
    private static final long serialVersionUID = 1L;
    private String loanId;
    private String customerId;
    private double principalAmount;
    private double interestRate; // Annual rate in percentage (e.g., 8.5)
    private int tenureInMonths;
    private double originalPrincipal;
    
    private double totalAmountPaid;
    private boolean isClosed;
    private LocalDate startDate;

    public Loan(String loanId, String customerId, double principalAmount, double interestRate, int tenureInMonths) {
        this.loanId = loanId;
        this.customerId = customerId;
        this.principalAmount = principalAmount;
        this.originalPrincipal = principalAmount; // Keep record of original
        this.interestRate = interestRate;
        this.tenureInMonths = tenureInMonths;
        this.totalAmountPaid = 0.0;
        this.isClosed = false;
        this.startDate = LocalDate.now();
    }

    public String getLoanId() { return loanId; }
    public String getCustomerId() { return customerId; }
    
    public double getPrincipalAmount() { return principalAmount; }
    
    public double getInterestRate() { return interestRate; }
    
    public int getTenureInMonths() { return tenureInMonths; }
    
    public boolean isClosed() { return isClosed; }

    public void makeRepayment(double amount) {
        if (isClosed) {
            throw new IllegalStateException("Loan is already closed.");
        }
        
        // Logic: Reduce principal (simplified logic, real logic handles interest portion in Service)
        this.principalAmount = this.principalAmount - amount;
        this.totalAmountPaid += amount;

        // Mutation Target: Change <= to <
        if (this.principalAmount <= 0) {
            this.principalAmount = 0;
            this.isClosed = true;
        }
    }

    public double getTotalAmountPaid() { return totalAmountPaid; }

    @Override
    public String toString() {
        return "Loan ID: " + loanId + "\n" +
               "Customer: " + customerId + "\n" +
               "Remaining Principal: " + String.format("%.2f", principalAmount) + "\n" +
               "Status: " + (isClosed ? "PAID OFF" : "ACTIVE");
    }
}