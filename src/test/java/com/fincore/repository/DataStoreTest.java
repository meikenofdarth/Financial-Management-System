package com.fincore.repository;

import com.fincore.model.Account;
import com.fincore.model.Customer;
import com.fincore.model.Loan;
import com.fincore.model.SavingsAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataStoreTest {

    private DataStore dataStore;

    @BeforeEach
    void setUp() {
        dataStore = DataStore.getInstance();
        dataStore.clearAll();
    }

    @Test
    void testGetCustomerByEmail() {
        Customer c1 = new Customer("C1", "A", "B", "find@me.com", "pass", 700, 500);
        Customer c2 = new Customer("C2", "X", "Y", "other@me.com", "pass", 700, 500);
        
        dataStore.addCustomer(c1);
        dataStore.addCustomer(c2);

        // Test Success (Kills loop condition mutants)
        Customer found = dataStore.getCustomerByEmail("find@me.com");
        assertNotNull(found);
        assertEquals("C1", found.getCustomerId());

        // Test Failure (Ensures loop finishes correctly)
        Customer notFound = dataStore.getCustomerByEmail("ghost@me.com");
        assertNull(notFound);
    }

    @Test
    void testGetAccountsForCustomer() {
        Account a1 = new SavingsAccount("A1", "C1", 100);
        Account a2 = new SavingsAccount("A2", "C1", 200);
        Account a3 = new SavingsAccount("A3", "C2", 300); // Different customer

        dataStore.addAccount(a1);
        dataStore.addAccount(a2);
        dataStore.addAccount(a3);

        List<Account> result = dataStore.getAccountsForCustomer("C1");
        
        // Assert Size (Kills conditional boundary mutants inside the loop)
        assertEquals(2, result.size());
        assertTrue(result.contains(a1));
        assertTrue(result.contains(a2));
        assertFalse(result.contains(a3));
    }

    @Test
    void testGetLoansForCustomer() {
        Loan l1 = new Loan("L1", "C1", 1000, 5, 12);
        Loan l2 = new Loan("L2", "C2", 1000, 5, 12);

        dataStore.addLoan(l1);
        dataStore.addLoan(l2);

        List<Loan> result = dataStore.getLoansForCustomer("C1");
        assertEquals(1, result.size());
        assertEquals("L1", result.get(0).getLoanId());
    }
}