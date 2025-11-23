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
        dataStore = DataStore.getInstance();
        dataStore.clearAll(); 
        accountService = new AccountService();
    }

    @Test
    void testDeposit_UpdatesBalanceAndHistory() {
        // Setup
        accountService.registerCustomer("C1", "John", "Doe", "j@d.com", "P@ss1234", 700, 50000);
        Account acc = accountService.createAccount("SAVINGS", "A1", "C1", 1000.0);
        
        int initialHistorySize = acc.getTransactionHistory().size(); // Should be 1 (Opening)

        // Action
        accountService.deposit("A1", 500.0);

        // Assertions
        assertEquals(1500.0, acc.getBalance(), "Balance should update");
        
        // CRITICAL: This kills VoidMethodCallMutator on 'addTransaction'
        assertEquals(initialHistorySize + 1, acc.getTransactionHistory().size(), "Transaction history must increase");
        assertEquals("DEPOSIT", acc.getTransactionHistory().get(initialHistorySize).getType());
    }

    @Test
    void testWithdraw_UpdatesBalanceAndHistory() {
        accountService.registerCustomer("C1", "John", "Doe", "j@d.com", "P@ss1234", 700, 50000);
        Account acc = accountService.createAccount("CURRENT", "A1", "C1", 1000.0);
        
        int initialHistorySize = acc.getTransactionHistory().size();

        accountService.withdraw("A1", 200.0);

        assertEquals(800.0, acc.getBalance());
        assertEquals(initialHistorySize + 1, acc.getTransactionHistory().size());
        assertEquals("WITHDRAWAL", acc.getTransactionHistory().get(initialHistorySize).getType());
    }

    @Test
    void testTransfer_FullIntegration() {
        accountService.registerCustomer("C1", "John", "Doe", "j@d.com", "P@ss1234", 700, 50000);
        Account src = accountService.createAccount("SAVINGS", "A1", "C1", 2000.0);
        Account dest = accountService.createAccount("SAVINGS", "A2", "C1", 500.0);

        accountService.transfer("A1", "A2", 500.0);

        // Verify Balances
        assertEquals(1500.0, src.getBalance());
        assertEquals(1000.0, dest.getBalance());

        // Verify Transactions (Kill Mutants that might skip one side of transfer)
        assertTrue(src.getTransactionHistory().stream().anyMatch(t -> t.getType().equals("TRANSFER_OUT")));
        assertTrue(dest.getTransactionHistory().stream().anyMatch(t -> t.getType().equals("TRANSFER_IN")));
    }
    
    @Test
    void testAccountCreation_Validation() {
        accountService.registerCustomer("C1", "John", "Doe", "j@d.com", "P@ss1234", 700, 50000);
        
        // Kills VoidMethodMutator in error handling
        assertThrows(IllegalArgumentException.class, () -> 
            accountService.createAccount("INVALID_TYPE", "AX", "C1", 100)
        );
    }
}