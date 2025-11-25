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
        //delete files
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        resetSingleton();
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

    private void resetSingleton() throws Exception {
        Field instance = DataStore.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testAddCustomerStored() throws Exception {
        Customer c = new Customer("C1", "Sanchit", "Kumar", "abcd@e.com", "Pass@123", 700, 500);
        DataStore.getInstance().addCustomer(c);
        resetSingleton(); 

        DataStore newDataStore = DataStore.getInstance();
        Customer cu = newDataStore.getCustomer("C1");
        assertNotNull(cu);
        assertEquals("abcd@e.com", cu.getEmail());
    }

    @Test
    void testAddAccountStored() throws Exception {
        Account a = new SavingsAccount("A1", "C1", 1000);
        DataStore.getInstance().addAccount(a);
        resetSingleton();
        
        Account ac = DataStore.getInstance().getAccount("A1");
        assertNotNull(ac);
        assertEquals(1000.0, ac.getBalance());
    }

    @Test
    void testAddLoanStored() throws Exception {
        Loan l = new Loan("L1", "C1", 5000, 5.0, 12);
        DataStore.getInstance().addLoan(l);
        resetSingleton();
        
        Loan l2 = DataStore.getInstance().getLoan("L1");
        assertNotNull(l2);
        assertEquals(5000.0, l2.getPrincipalAmount());
    }

    @Test
    void testGetCustomerByEmail() {
        Customer c1 = new Customer("C1", "A", "B", "abcd@e.com", "Pass@123", 700, 500);
        DataStore.getInstance().addCustomer(c1);

        Customer c2 = DataStore.getInstance().getCustomerByEmail("abcd@e.com");
        assertNotNull(c2);
        assertEquals("C1", c2.getCustomerId());

        Customer c3 = DataStore.getInstance().getCustomerByEmail("abcdefg@e.com");
        assertNull(c3);
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
    void testLoadDataHandlesMissingFile() throws Exception {
        //If no bank_data.ser file exists
        File f = new File(FILE_PATH);
        if(f.exists()) f.delete();
        resetSingleton();
        
        DataStore ds = DataStore.getInstance();
        assertNotNull(ds);
        assertTrue(ds.getAllCustomers().isEmpty());
    }
    
    @Test
    void testGetAllCustomers() {
        DataStore ds = DataStore.getInstance();
        ds.addCustomer(new Customer("C1", "A", "A", "a@gmail.com", "Pass@123", 1, 1));
        ds.addCustomer(new Customer("C2", "B", "B", "b@gmail.com", "Pass@123", 1, 1));
        
        assertEquals(2, ds.getAllCustomers().size());
    }
}