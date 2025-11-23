package com.fincore.repository;

import com.fincore.model.Account;
import com.fincore.model.Customer;
import com.fincore.model.Loan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory database to store Customers, Accounts, and Loans.
 * Implements Singleton pattern to ensure a single source of truth.
 */
public class DataStore {
    private static DataStore instance;

    private Map<String, Customer> customers;
    private Map<String, Account> accounts;
    private Map<String, Loan> loans;

    private DataStore() {
        customers = new HashMap<>();
        accounts = new HashMap<>();
        loans = new HashMap<>();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // Customer Operations
    public void addCustomer(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    public Customer getCustomer(String customerId) {
        return customers.get(customerId);
    }
    
    public Customer getCustomerByEmail(String email) {
        // Linear search - good for creating loops for testing
        for (Customer c : customers.values()) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                return c;
            }
        }
        return null;
    }

    // Account Operations
    public void addAccount(Account account) {
        accounts.put(account.getAccountId(), account);
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    public List<Account> getAccountsForCustomer(String customerId) {
        List<Account> customerAccounts = new ArrayList<>();
        for (Account acc : accounts.values()) {
            if (acc.getCustomerId().equals(customerId)) {
                customerAccounts.add(acc);
            }
        }
        return customerAccounts;
    }

    // Loan Operations
    public void addLoan(Loan loan) {
        loans.put(loan.getLoanId(), loan);
    }

    public Loan getLoan(String loanId) {
        return loans.get(loanId);
    }
    
    public List<Loan> getLoansForCustomer(String customerId) {
        List<Loan> customerLoans = new ArrayList<>();
        for (Loan l : loans.values()) {
            if (l.getCustomerId().equals(customerId)) {
                customerLoans.add(l);
            }
        }
        return customerLoans;
    }

    // Utility to clear data (useful for unit test teardown)
    public void clearAll() {
        customers.clear();
        accounts.clear();
        loans.clear();
    }
}