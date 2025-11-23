package com.fincore.service;

import com.fincore.model.Account;
import com.fincore.model.Customer;
import com.fincore.repository.DataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private AccountService accountService;
    private DataStore dataStore;

    @BeforeEach
    void setUp() {
        // Reset the Singleton state before every test to ensure isolation
        dataStore = DataStore.getInstance();
        dataStore.clearAll(); 
        accountService = new AccountService();
    }

    @Test
    void testRegisterCustomer_Success() {
        Customer c = accountService.registerCustomer("C1", "John", "Doe", "john@test.com", "Pass@123", 750, 50000);
        assertNotNull(c);
        assertEquals("john@test.com", dataStore.getCustomer("C1").getEmail());
    }

    @Test
    void testCreateAccount_Savings() {
        // Setup
        accountService.registerCustomer("C1", "John", "Doe", "john@test.com", "Pass@123", 750, 50000);
        
        // Action
        Account acc = accountService.createAccount("SAVINGS", "A1", "C1", 1000.0);
        
        // Assertion
        assertNotNull(acc);
        assertEquals("SAVINGS", acc.getAccountType());
        assertEquals(1000.0, acc.getBalance());
    }

    @Test
    void testTransfer_Success() {
        // 1. Setup Data
        accountService.registerCustomer("C1", "John", "Doe", "john@test.com", "Pass@123", 750, 50000);
        accountService.createAccount("SAVINGS", "A1", "C1", 2000.0);
        accountService.createAccount("SAVINGS", "A2", "C1", 500.0);

        // 2. Perform Transfer
        accountService.transfer("A1", "A2", 500.0);

        // 3. Verify Integration (A1 decreased, A2 increased)
        // Kills mutant: Removing 'to.deposit(amount)' in source code
        assertEquals(1500.0, dataStore.getAccount("A1").getBalance());
        assertEquals(1000.0, dataStore.getAccount("A2").getBalance());
    }

    @Test
    void testTransfer_InsufficientFunds() {
        accountService.registerCustomer("C1", "John", "Doe", "john@test.com", "Pass@123", 750, 50000);
        accountService.createAccount("SAVINGS", "A1", "C1", 100.0); // Only 100
        accountService.createAccount("SAVINGS", "A2", "C1", 500.0);

        // Expect Exception
        assertThrows(IllegalStateException.class, () -> 
            accountService.transfer("A1", "A2", 200.0)
        );

        // Verify balances didn't change (Atomic transaction check)
        assertEquals(100.0, dataStore.getAccount("A1").getBalance());
        assertEquals(500.0, dataStore.getAccount("A2").getBalance());
    }
}