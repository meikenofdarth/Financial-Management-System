package com.fincore.repository;

import com.fincore.model.Account;
import com.fincore.model.Customer;
import com.fincore.model.Loan;
import com.fincore.model.SavingsAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataStoreTest {

    private static final String FILE_PATH = "bank_data.ser";

    @BeforeEach
    void setUp() throws Exception {
        // 1. Delete file to start clean
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        
        // 2. Reset Singleton using Reflection (Crucial for mutation testing)
        resetSingleton();
        
        // 3. Initialize fresh
        DataStore.getInstance().clearAll();
    }

    @AfterEach
    void tearDown() throws Exception {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        resetSingleton();
    }

    // HELPER: Forces the Singleton 'instance' to null so next call triggers loadData()
    private void resetSingleton() throws Exception {
        Field instance = DataStore.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testAddCustomer_PersistsToDisk() throws Exception {
        // 1. Add Data
        Customer c = new Customer("C1", "Save", "Me", "save@me.com", "pass", 700, 500);
        DataStore.getInstance().addCustomer(c);
        
        // 2. WIPE MEMORY (Reset Singleton)
        resetSingleton(); 
        
        // 3. Reload from Disk (Calls loadData)
        DataStore newDataStore = DataStore.getInstance();
        
        // 4. Verify (If saveData() was deleted by mutant, this fails)
        Customer ret = newDataStore.getCustomer("C1");
        assertNotNull(ret, "Customer should persist after reload");
        assertEquals("save@me.com", ret.getEmail());
    }

    @Test
    void testAddAccount_PersistsToDisk() throws Exception {
        Account a = new SavingsAccount("A1", "C1", 1000);
        DataStore.getInstance().addAccount(a);
        
        // WIPE MEMORY
        resetSingleton();
        
        // Reload
        Account ret = DataStore.getInstance().getAccount("A1");
        assertNotNull(ret, "Account should persist after reload");
        assertEquals(1000.0, ret.getBalance());
    }

    @Test
    void testAddLoan_PersistsToDisk() throws Exception {
        Loan l = new Loan("L1", "C1", 5000, 5.0, 12);
        DataStore.getInstance().addLoan(l);
        
        // WIPE MEMORY
        resetSingleton();
        
        // Reload
        Loan ret = DataStore.getInstance().getLoan("L1");
        assertNotNull(ret, "Loan should persist after reload");
        assertEquals(5000.0, ret.getPrincipalAmount());
    }

    @Test
    void testGetCustomerByEmail() {
        Customer c1 = new Customer("C1", "A", "B", "find@me.com", "pass", 700, 500);
        DataStore.getInstance().addCustomer(c1);

        Customer found = DataStore.getInstance().getCustomerByEmail("find@me.com");
        assertNotNull(found);
        assertEquals("C1", found.getCustomerId());

        Customer notFound = DataStore.getInstance().getCustomerByEmail("ghost@me.com");
        assertNull(notFound);
    }

    @Test
    void testGetAccountsForCustomer() {
        Account a1 = new SavingsAccount("A1", "C1", 100);
        Account a2 = new SavingsAccount("A2", "C1", 200);
        
        DataStore ds = DataStore.getInstance();
        ds.addAccount(a1);
        ds.addAccount(a2);

        List<Account> result = ds.getAccountsForCustomer("C1");
        assertEquals(2, result.size());
    }

    @Test
    void testGetLoansForCustomer() {
        Loan l1 = new Loan("L1", "C1", 1000, 5, 12);
        DataStore.getInstance().addLoan(l1);

        List<Loan> result = DataStore.getInstance().getLoansForCustomer("C1");
        assertEquals(1, result.size());
    }
    
    @Test
    void testLoadData_HandlesMissingFile() throws Exception {
        // Ensure no file exists
        File f = new File(FILE_PATH);
        if(f.exists()) f.delete();
        
        resetSingleton();
        
        // Should not crash, just return empty store
        DataStore ds = DataStore.getInstance();
        assertNotNull(ds);
        assertTrue(ds.getAllCustomers().isEmpty());
    }
    
    @Test
    void testGetAllCustomers() {
        DataStore ds = DataStore.getInstance();
        ds.addCustomer(new Customer("C1", "A", "B", "e1", "p", 1, 1));
        ds.addCustomer(new Customer("C2", "X", "Y", "e2", "p", 1, 1));
        
        assertEquals(2, ds.getAllCustomers().size());
    }
}