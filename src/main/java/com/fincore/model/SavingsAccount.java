package com.fincore.model;

/**
 * A Savings Account that earns interest but has a minimum balance requirement.
 */
public class SavingsAccount extends Account {
    // Constants are great for testing if values are hardcoded correctly
    private static final double MIN_BALANCE = 500.00;
    private static final double INTEREST_RATE = 0.04; // 4% per annum

    public SavingsAccount(String accountId, String customerId, double openingBalance) {
        super(accountId, customerId, openingBalance);
    }

    @Override
    public boolean canWithdraw(double amount) {
        // Mutation Opportunity: Change '>' to '>=' or '-' to '+'
        return this.isActive && (this.balance - amount) >= MIN_BALANCE;
    }

    @Override
    public void applyEndOfMonthBenefits() {
        if (!isActive) return;

        // Logic: Only apply interest if balance is positive
        if (balance > 0) {
            // Calculate monthly interest (Annual / 12)
            double monthlyInterest = balance * (INTEREST_RATE / 12.0);
            
            // Rounding to 2 decimal places (Validation logic)
            monthlyInterest = Math.round(monthlyInterest * 100.0) / 100.0;
            
            if (monthlyInterest > 0) {
                this.balance += monthlyInterest;
                addTransaction(new Transaction(
                    "INT-" + System.currentTimeMillis(), 
                    this.accountId, 
                    "INTEREST", 
                    monthlyInterest, 
                    "Monthly Savings Interest Applied"
                ));
            }
        }
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }

    public double getInterestRate() {
        return INTEREST_RATE;
    }

    @Override
    public String toString() {
        return String.format("Savings Account [%s] | Balance: $%.2f | Cust: %s | Status: %s", 
            accountId, balance, customerId, (isActive ? "Active" : "Closed"));
    }
}