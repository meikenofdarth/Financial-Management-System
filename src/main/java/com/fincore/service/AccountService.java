package com.fincore.service;

import com.fincore.model.*;
import com.fincore.repository.DataStore;
import com.fincore.util.ValidationUtil;

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

        // Logic: Manually updating balance (no setter) to simulate transaction logic
        // We could access balance directly if it were public, but here we might need a method in Account or update directly via reflection/package-private
        // For this design, let's assume we implement a deposit method in Account or handle it here via casting if fields were public.
        // To keep encapsulation, let's add a synchronized block here or assume single thread for simplicity.
        // NOTE: In the Account model earlier, we didn't add a deposit method. 
        // Real-world: Add deposit() to Account. For this project, let's assume we modify Account.java or do this:
        
        // *IMPORTANT*: Ideally, go back to Account.java and add a `deposit(double amount)` method.
        // Since we are writing files linearly, let's assume we are handling it via a method we added or simple logic if fields were accessible.
        // Let's rely on a helper or assume we can modify the state.
        // EDIT: For the sake of this code block working with the previous one, we will cast or use a hack, 
        // BUT the best practice is to add `public void deposit(double amount)` to Account.java.
        // I will assume we added `public void deposit(double amount)` to Account.java for this to work cleanly.
        
        // Let's implement the logic assuming we can access specific methods or we update the object:
        // Since `balance` is protected in `Account`, and `AccountService` is in a different package, we cannot access it directly.
        // FIX: We need a `deposit` method in `Account.java`. 
        // *Correction*: Since I can't edit previous messages, let's implement the logic here using a public method we *should* have added.
        // Please add `public void deposit(double amount) { this.balance += amount; }` to your Account.java abstract class.
        
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
            // Again, assumes a `withdraw` method exists or we use `deposit(-amount)`
            // Please add `public void withdraw(double amount) { this.balance -= amount; }` to Account.java
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
     * This is CRITICAL for Integration Testing.
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