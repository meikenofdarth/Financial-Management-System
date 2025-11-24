package com.fincore.service;

import com.fincore.model.Customer;
import com.fincore.model.Loan;
import com.fincore.repository.DataStore;
import com.fincore.util.FinancialMath;

public class LoanService {
    private DataStore dataStore;

    public LoanService() {
        this.dataStore = DataStore.getInstance();
    }

    /**
     * Applies for a loan.
     * Logic:
     * 1. Credit Score must be >= 600.
     * 2. Loan amount must not exceed 5x yearly income.
     */
    public Loan applyForLoan(String loanId, String customerId, double amount, int months) {
        Customer customer = dataStore.getCustomer(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        // Rule 1: Credit Score Check
        if (customer.getCreditScore() < 600) {
            throw new IllegalStateException("Credit score too low for approval");
        }

        // Rule 2: Income Check
        double maxLoanAmount = customer.getYearlyIncome() * 5;
        if (amount > maxLoanAmount) {
            throw new IllegalStateException("Loan amount exceeds income eligibility limits");
        }

        // Determine Interest Rate based on Credit Score
        double rate;
        if (customer.getCreditScore() >= 750) {
            rate = 6.5; // Prime rate
        } else if (customer.getCreditScore() >= 700) {
            rate = 7.5;
        } else {
            rate = 9.0;
        }

        Loan loan = new Loan(loanId, customerId, amount, rate, months);
        dataStore.addLoan(loan);
        
        System.out.println("Loan Approved! EMI will be: " + 
            FinancialMath.calculateEMI(amount, rate, months));
            
        return loan;
    }
    
    /**
     * Process a repayment.
     */
    public void payLoanInstallment(String loanId, double amount) {
        Loan loan = dataStore.getLoan(loanId);
        if (loan == null) throw new IllegalArgumentException("Loan not found");
        
        if (loan.isClosed()) {
            throw new IllegalStateException("Loan is already paid off");
        }
        loan.makeRepayment(amount);
    }
}