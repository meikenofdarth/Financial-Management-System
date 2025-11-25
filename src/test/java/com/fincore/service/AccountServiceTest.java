package com.fincore.service;

import com.fincore.model.Account;
import com.fincore.repository.DataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private AccountService accountService;
    private DataStore dataStore;

    @BeforeEach
    void setUp() {
        //initialize data store and account service before each test
        dataStore = DataStore.getInstance();
        dataStore.clearAll(); 
        accountService = new AccountService();
    }

    @Test
    void testAccountCreation() {
        //test account creation
        accountService.registerCustomer("C1", "Abcd", "Wasd", "was@d.com", "Psss@123", 700, 50000);
        assertThrows(IllegalArgumentException.class, () -> 
            accountService.createAccount("INVALID_TYPE", "AX", "C1", 100)
        );
    }

    @Test
    void testDeposits() {
        //test deposits
        accountService.registerCustomer("C1", "Abcd", "Wasd", "was@d.com", "Psss@123", 700, 50000);
        Account acc = accountService.createAccount("SAVINGS", "A1", "C1", 1000.0);
        int initialHistorySize = acc.getTransactionHistory().size();
        accountService.deposit("A1", 500.0);
        assertEquals(1500.0, acc.getBalance());
        assertEquals(initialHistorySize + 1, acc.getTransactionHistory().size());
        assertEquals("DEPOSIT", acc.getTransactionHistory().get(initialHistorySize).getType());
    }

    @Test
    void testWithdrawals() {
        //test withdrawals
        accountService.registerCustomer("C1", "Abcd", "Wasd", "was@d.com", "Psss@123", 700, 50000);
        Account acc = accountService.createAccount("CURRENT", "A1", "C1", 1000.0);
        int initialHistorySize = acc.getTransactionHistory().size();
        accountService.withdraw("A1", 200.0);
        assertEquals(800.0, acc.getBalance());
        assertEquals(initialHistorySize + 1, acc.getTransactionHistory().size());
        assertEquals("WITHDRAWAL", acc.getTransactionHistory().get(initialHistorySize).getType());
    }

    @Test
    void testTransfers() {
        //test transfers
        accountService.registerCustomer("C1", "Abcd", "Wasd", "was@d.com", "Psss@123", 700, 50000);
        Account src = accountService.createAccount("SAVINGS", "A1", "C1", 2000.0);
        Account dest = accountService.createAccount("SAVINGS", "A2", "C1", 500.0);
        accountService.transfer("A1", "A2", 500.0);
        assertEquals(1500.0, src.getBalance());
        assertEquals(1000.0, dest.getBalance());

        //verify transaction histories
        assertTrue(src.getTransactionHistory().stream().anyMatch(t -> t.getType().equals("TRANSFER_OUT")));
        assertTrue(dest.getTransactionHistory().stream().anyMatch(t -> t.getType().equals("TRANSFER_IN")));
    }   
}