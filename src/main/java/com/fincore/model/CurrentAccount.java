package com.fincore.model;

/**
 * A Current Account meant for frequent transactions with Overdraft facility.
 */
public class CurrentAccount extends Account {
    private double overdraftLimit;

    public CurrentAccount(String accountId, String customerId, double openingBalance, double overdraftLimit) {
        super(accountId, customerId, openingBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public boolean canWithdraw(double amount) {
        if (!isActive) return false;
        
        // Logic: Can withdraw if balance + overdraft covers the amount
        return (this.balance + this.overdraftLimit) >= amount;
    }

    @Override
    public void applyEndOfMonthBenefits() {
        // Current accounts might have a maintenance fee if balance is low
        if (balance < 0) {
            double overdraftFee = 25.00;
            this.balance -= overdraftFee;
            addTransaction(new Transaction(
                "FEE-" + System.currentTimeMillis(), 
                this.accountId, 
                "FEE", 
                overdraftFee, 
                "Overdraft Usage Fee"
            ));
        }
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public String getAccountType() {
        return "CURRENT";
    }
    
    @Override
    public String toString() {
        return String.format("Current Account [%s] | Balance: $%.2f | Overdraft Limit: $%.2f", 
            accountId, balance, overdraftLimit);
    }
}