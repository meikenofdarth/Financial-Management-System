package com.fincore.repository;

import com.fincore.model.Account;
import com.fincore.model.Customer;
import com.fincore.model.Loan;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persisted database using Java Serialization.
 * Stores data in 'bank_data.ser' in the project root.
 */
public class DataStore implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final String FILE_PATH = "bank_data.ser";
    
    // The instance is not transient, but we handle it manually in getInstance
    private static transient DataStore instance;

    private Map<String, Customer> customers;
    private Map<String, Account> accounts;
    private Map<String, Loan> loans;

    // Private constructor for Singleton
    private DataStore() {
        customers = new HashMap<>();
        accounts = new HashMap<>();
        loans = new HashMap<>();
    }

    /**
     * Loads data from file if it exists, otherwise creates a new instance.
     */
    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = loadData();
        }
        return instance;
    }

    // --- Persistence Methods ---

    // Save the current state of this object to a file
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(this);
            // System.out.println("DEBUG: Data saved to disk."); // Uncomment for debugging
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    // Load the object from the file
    private static DataStore loadData() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (DataStore) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading existing data. Starting fresh. Error: " + e.getMessage());
            }
        }
        return new DataStore();
    }

    // --- Customer Operations ---
    
    public void addCustomer(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
        saveData(); // <--- This saves the data immediately
    }

    public Customer getCustomer(String customerId) {
        return customers.get(customerId);
    }
    
    public Customer getCustomerByEmail(String email) {
        for (Customer c : customers.values()) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                return c;
            }
        }
        return null;
    }
    
    // Helper for Admin to see all users
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    // --- Account Operations ---
    
    public void addAccount(Account account) {
        accounts.put(account.getAccountId(), account);
        saveData(); // <--- Save
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

    // --- Loan Operations ---
    
    public void addLoan(Loan loan) {
        loans.put(loan.getLoanId(), loan);
        saveData(); // <--- Save
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
        saveData();
    }
}