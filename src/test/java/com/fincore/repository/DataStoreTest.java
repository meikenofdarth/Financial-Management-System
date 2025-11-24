package com.fincore.repository;

import com.fincore.model.Account;
import com.fincore.model.Customer;
import com.fincore.model.Loan;
import com.fincore.model.SavingsAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataStoreTest {

    private DataStore dataStore;
    private static final String FILE_PATH = "bank_data.ser";

    @BeforeEach
    void setUp() {
        // CRITICAL: Delete the file before test starts to ensure clean state
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        
        // Force reset the singleton (Logic depends on your implementation)
        // Since we can't easily nullify the instance in Singleton without reflection,
        // we rely on clearAll() and file deletion.
        dataStore = DataStore.getInstance();
        dataStore.clearAll(); // Clears memory AND disk
    }

    @AfterEach
    void tearDown() {
        // Cleanup after test
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testPersistence() {
        // 1. Create Data
        Customer c = new Customer("C_PERSIST", "Save", "Me", "save@me.com", "pass", 700, 500);
        dataStore.addCustomer(c);
        
        // 2. Simulate App Restart (Reload from disk)
        // We do this by calling loadData() logic indirectly or verifying file exists
        File file = new File(FILE_PATH);
        assertTrue(file.exists(), "Data file should be created after adding customer");
        assertTrue(file.length() > 0, "Data file should not be empty");
        
        // 3. Verify Data in Memory matches
        Customer ret = dataStore.getCustomer("C_PERSIST");
        assertNotNull(ret);
        assertEquals("Save", ret.getFirstName());
    }

    @Test
    void testGetCustomerByEmail() {
        Customer c1 = new Customer("C1", "A", "B", "find@me.com", "pass", 700, 500);
        dataStore.addCustomer(c1);

        Customer found = dataStore.getCustomerByEmail("find@me.com");
        assertNotNull(found);
        assertEquals("C1", found.getCustomerId());

        Customer notFound = dataStore.getCustomerByEmail("ghost@me.com");
        assertNull(notFound);
    }

    @Test
    void testGetAccountsForCustomer() {
        Account a1 = new SavingsAccount("A1", "C1", 100);
        Account a2 = new SavingsAccount("A2", "C1", 200);
        
        dataStore.addAccount(a1);
        dataStore.addAccount(a2);

        List<Account> result = dataStore.getAccountsForCustomer("C1");
        assertEquals(2, result.size());
    }

    @Test
    void testGetLoansForCustomer() {
        Loan l1 = new Loan("L1", "C1", 1000, 5, 12);
        dataStore.addLoan(l1);

        List<Loan> result = dataStore.getLoansForCustomer("C1");
        assertEquals(1, result.size());
    }
    @Test
        void testLoanPersistence() {
            // 1. Add Loan
            Loan l = new Loan("L_SAVE", "C1", 5000, 5.0, 12);
            dataStore.addLoan(l);
            
            // 2. Force Reload (Simulate App Restart)
            // We use reflection or just call the package-private loadData if accessible, 
            // OR just clear memory and rely on getInstance reloading.
            // Since getInstance checks if instance is null, we can't easily force it null.
            // BUT, we can check the file size!
            
            java.io.File file = new java.io.File("bank_data.ser");
            assertTrue(file.length() > 0, "File should grow after saving loan");
            
            // OR (Better):
            // Manually deserializing to verify content without relying on Singleton instance logic
            try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.FileInputStream(file))) {
                DataStore diskStore = (DataStore) ois.readObject();
                assertNotNull(diskStore.getLoan("L_SAVE"), "Loan must persist to disk");
            } catch (Exception e) {
                fail("Failed to read disk data: " + e.getMessage());
            }
        }
}