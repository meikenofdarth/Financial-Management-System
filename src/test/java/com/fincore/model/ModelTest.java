package com.fincore.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void testCustomer() {
        Customer c = new Customer("C1", "Anurag", "Ramaswamy", "abcd@gmail.com", "Abcdefg#$", 700, 50000);
        
        //Test Getters
        assertEquals("C1", c.getCustomerId());
        assertEquals("Anurag", c.getFirstName());
        assertEquals("Ramaswamy", c.getLastName());
        assertEquals("Anurag Ramaswamy", c.getFullName());
        assertEquals("abcd@gmail.com", c.getEmail());
        assertTrue(c.verifyPassword("Abcdefg#$"));
        assertEquals(700, c.getCreditScore());
        assertEquals(50000, c.getYearlyIncome());
        
        //Test Setters
        c.setEmail("abcdef@gmail.com");
        c.setPhoneNumber("1234567890");
        c.setCreditScore(800);
        assertEquals("abcdef@gmail.com", c.getEmail());
        assertEquals("1234567890", c.getPhoneNumber());
        assertEquals(800, c.getCreditScore());
        
        //Null check
        String str = c.toString();
        assertNotNull(str);
        assertTrue(str.contains("C1"));
    }

    @Test
    void testAccount() {
        // Savings
        SavingsAccount sa = new SavingsAccount("1", "C1", 1000.0);
        
        //Test Getters
        assertEquals("1", sa.getAccountId());
        assertEquals("C1", sa.getCustomerId());
        assertTrue(sa.isActive());
        assertEquals("SAVINGS", sa.getAccountType());
        assertEquals(0.04, sa.getInterestRate());
        
        sa.closeAccount();
        assertFalse(sa.isActive());
        
        //toString
        String s = sa.toString();
        assertTrue(s.contains("1"));
        assertTrue(s.contains("Closed"));

        // Current
        CurrentAccount ca = new CurrentAccount("2", "C1", 0.0, 500.0);

        assertEquals("CURRENT", ca.getAccountType());
        ca.setOverdraftLimit(600.0);
        assertEquals(600.0, ca.getOverdraftLimit());
        String str = ca.toString();
        assertTrue(str.contains("2"));
    }

    @Test
    void testLoan() {
        Loan l = new Loan("L1", "C1", 1000.0, 5.0, 12);
        
        assertEquals("L1", l.getLoanId());
        assertEquals("C1", l.getCustomerId());
        assertEquals(1000.0, l.getPrincipalAmount());
        assertEquals(5.0, l.getInterestRate());
        assertEquals(12, l.getTenureInMonths());
        assertEquals(0.0, l.getTotalAmountPaid());
        String s = l.toString();
        assertTrue(s.contains("L1"));
        assertTrue(s.contains("ACTIVE"));
    }

    @Test
    void testTransaction() {
        Transaction t = new Transaction("T1", "A1", "DEP", 100.0, "Desc");
        
        assertEquals("T1", t.getTransactionId());
        assertEquals("A1", t.getAccountId());
        assertEquals("DEP", t.getType());
        assertEquals(100.0, t.getAmount());
        assertEquals("Desc", t.getDescription());
        assertNotNull(t.getTimestamp());
        
        String str = t.toString();
        assertTrue(str.contains("T1"));
        assertTrue(str.contains("Desc"));
    }

    //SavingsAccount Tests
    @Test
    void testSavingsAccountWithdrawBoundaries() {
        //Min balance = 500
        SavingsAccount sa = new SavingsAccount("S1", "C1", 1000.0);
        assertTrue(sa.canWithdraw(400.0));
        assertTrue(sa.canWithdraw(500.0));
        assertFalse(sa.canWithdraw(500.01));
    }

    @Test
    void testSavingsAccountInactiveWithdraw() {
        SavingsAccount sa = new SavingsAccount("S1", "C1", 1000.0);
        sa.closeAccount();
        assertFalse(sa.canWithdraw(100.0));
    }

    @Test
    void testSavingsAccountInterestCalculation() {
        SavingsAccount sa = new SavingsAccount("S1", "C1", 1200.0);
        int x = sa.getTransactionHistory().size();
        sa.applyEndOfMonthBenefits();
        assertEquals(1204.00, sa.getBalance(), 0.001);
        assertEquals(x + 1, sa.getTransactionHistory().size());
        assertEquals("INTEREST", sa.getTransactionHistory().get(x).getType());
    }

    @Test
    void testSavingsAccountInterestEdgeCases() {
        SavingsAccount saClosed = new SavingsAccount("S2", "C1", 1200.0);
        saClosed.closeAccount();
        saClosed.applyEndOfMonthBenefits();
        assertEquals(1200.0, saClosed.getBalance());

        SavingsAccount saZero = new SavingsAccount("S3", "C1", 0.0);
        saZero.applyEndOfMonthBenefits();
        assertEquals(0.0, saZero.getBalance());
        
        SavingsAccount saNeg = new SavingsAccount("S4", "C1", -100.0);
        saNeg.applyEndOfMonthBenefits();
        assertEquals(-100.0, saNeg.getBalance());
    }

    //CurrentAccount Tests
    @Test
    void testCurrentAccountWithdrawBoundaries() {
       
        CurrentAccount ca = new CurrentAccount("C1", "U1", 1000.0, 500.0);

        assertTrue(ca.canWithdraw(900.0));
        assertTrue(ca.canWithdraw(1001.0));
        assertTrue(ca.canWithdraw(1500.0));
        assertFalse(ca.canWithdraw(1500.01));
    }

    @Test
    void testCurrentAccountInactiveWithdraw() {
        CurrentAccount ca = new CurrentAccount("C1", "U1", 1000.0, 500.0);
        ca.closeAccount();
        assertFalse(ca.canWithdraw(100.0));
    }

    @Test
    void testCurrentAccountEndOfMonthApplyFee() {
        CurrentAccount ca = new CurrentAccount("C1", "U1", -100.0, 500.0);
        int x = ca.getTransactionHistory().size();
        ca.applyEndOfMonthBenefits();
        
        assertEquals(-125.0, ca.getBalance(), 0.001);
        assertEquals(x + 1, ca.getTransactionHistory().size());
        Transaction t = ca.getTransactionHistory().get(x);
        assertEquals("FEE", t.getType());
        assertEquals(25.0, t.getAmount(), 0.001);
        CurrentAccount caPos = new CurrentAccount("C2", "U1", 100.0, 500.0);
        caPos.applyEndOfMonthBenefits();
        assertEquals(100.0, caPos.getBalance());
        CurrentAccount caZero = new CurrentAccount("C3", "U1", 0.0, 500.0);
        caZero.applyEndOfMonthBenefits();
        assertEquals(0.0, caZero.getBalance());
    }

    @Test
    void testCurrentAccountSetOverdraftCheck() {
        CurrentAccount ca = new CurrentAccount("C1", "U1", 0.0, 0.0);
        ca.setOverdraftLimit(200.0);
        assertEquals(200.0, ca.getOverdraftLimit(), 0.001);
        assertTrue(ca.canWithdraw(150.0));
        assertFalse(ca.canWithdraw(250.0));
    }

    @Test
    void testCurrentAccountToString() {
        CurrentAccount ca = new CurrentAccount("C67", "CU67", 500, 200.00);
        String str = ca.toString();
        assertTrue(str.contains("C67"));
        assertTrue(str.contains("500"));
        assertTrue(str.contains("200.00"));
        assertTrue(str.contains("Current Account"));
    }
}