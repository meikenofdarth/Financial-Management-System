package com.fincore.service;

import com.fincore.model.*;
import com.fincore.repository.DataStore;
import com.fincore.util.ValidationUtil;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private DataStore dataStore;

    public AccountService() {
        this.dataStore = DataStore.getInstance();
    }

    /**
     * Creates a new Customer.
     */
    public Customer registerCustomer(String id, String first, String last, String email, String pass, int score, double income) {
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid Email Format");
        }
        if (!ValidationUtil.isStrongPassword(pass)) {
            throw new IllegalArgumentException("Password is too weak");
        }
        
        Customer newCustomer = new Customer(id, first, last, email, pass, score, income);
        dataStore.addCustomer(newCustomer);
        return newCustomer;
    }

    /**
     * Creates an account for an existing customer.
     */
    public Account createAccount(String type, String accountId, String customerId, double initialDeposit) {
        Customer c = dataStore.getCustomer(customerId);
        if (c == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        Account account;
        if ("SAVINGS".equalsIgnoreCase(type)) {
            account = new SavingsAccount(accountId, customerId, initialDeposit);
        } else if ("CURRENT".equalsIgnoreCase(type)) {
            // Default overdraft limit of 500 for new accounts
            account = new CurrentAccount(accountId, customerId, initialDeposit, 500.00);
        } else {
            throw new IllegalArgumentException("Invalid Account Type");
        }

        dataStore.addAccount(account);
        return account;
    }

    /**
     * Deposits money into an account.
     */
    public void deposit(String accountId, double amount) {
        if (!ValidationUtil.isPositiveAmount(amount)) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account account = dataStore.getAccount(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account does not exist");
        }

        if (!account.isActive()) {
            throw new IllegalStateException("Account is closed");
        }        
        account.deposit(amount); 
        
        Transaction t = new Transaction("DEP-" + System.currentTimeMillis(), accountId, "DEPOSIT", amount, "Cash Deposit");
        account.addTransaction(t);
        dataStore.addAccount(account); 
    }

    /**
     * Withdraws money from an account.
     */
    public void withdraw(String accountId, double amount) {
        Account account = dataStore.getAccount(accountId);
        if (account == null) throw new IllegalArgumentException("Account not found");

        if (account.canWithdraw(amount)) {
            account.withdraw(amount);
            
            Transaction t = new Transaction("WTH-" + System.currentTimeMillis(), accountId, "WITHDRAWAL", amount, "Cash Withdrawal");
            account.addTransaction(t);
            dataStore.addAccount(account);
        } else {
            throw new IllegalStateException("Insufficient funds or limit reached");
        }
    }

    /**
     * Transfers money between two accounts.
     */
    public void transfer(String fromAccId, String toAccId, double amount) {
            if (fromAccId.equals(toAccId)) {
                throw new IllegalArgumentException("Cannot transfer to same account");
            }

            Account from = dataStore.getAccount(fromAccId);
            Account to = dataStore.getAccount(toAccId);

            if (from == null || to == null) {
                throw new IllegalArgumentException("One or both accounts not found");
            }

            // Integration Logic: Check source
            if (from.canWithdraw(amount)) {
                // Step 1: Deduct from source in memory
                from.withdraw(amount);
                from.addTransaction(new Transaction("TRF-OUT-" + System.currentTimeMillis(), fromAccId, "TRANSFER_OUT", amount, "To " + toAccId));

                // Step 2: Add to destination in memory
                to.deposit(amount);
                to.addTransaction(new Transaction("TRF-IN-" + System.currentTimeMillis(), toAccId, "TRANSFER_IN", amount, "From " + fromAccId));
                
                // Step 3: PERSIST CHANGES TO FILE
                // We re-add the accounts to the DataStore to trigger the 'saveData()' method.
                dataStore.addAccount(from);
                dataStore.addAccount(to);
                
            } else {
                throw new IllegalStateException("Transfer failed: Insufficient funds");
            }
        }
}