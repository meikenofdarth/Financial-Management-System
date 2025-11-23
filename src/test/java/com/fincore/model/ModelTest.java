package com.fincore.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void testCustomerModel() {
        Customer c = new Customer("C1", "John", "Doe", "j@d.com", "pass", 700, 50000);
        
        // Exercise all getters to cover lines and kill "EmptyReturn" mutants
        assertEquals("C1", c.getCustomerId());
        assertEquals("John Doe", c.getFullName());
        assertEquals("j@d.com", c.getEmail());
        assertTrue(c.verifyPassword("pass"));
        assertEquals(700, c.getCreditScore());
        assertEquals(50000, c.getYearlyIncome());
        
        // Exercise Setters
        c.setPhoneNumber("1234567890");
        assertEquals("1234567890", c.getPhoneNumber());
        
        c.setEmail("new@d.com");
        assertEquals("new@d.com", c.getEmail());
        
        // Boundary check on Credit Score Setter
        c.setCreditScore(900); // Invalid, should ignore
        assertEquals(700, c.getCreditScore());
        c.setCreditScore(800); // Valid
        assertEquals(800, c.getCreditScore());
        
        assertNotNull(c.toString());
    }

    @Test
    void testTransactionModel() {
        Transaction t = new Transaction("T1", "A1", "DEP", 100.0, "Test");
        assertEquals("T1", t.getTransactionId());
        assertEquals("A1", t.getAccountId());
        assertEquals("DEP", t.getType());
        assertEquals(100.0, t.getAmount());
        assertEquals("Test", t.getDescription());
        assertNotNull(t.getTimestamp());
        assertNotNull(t.toString());
    }

    @Test
    void testLoanModel() {
        Loan l = new Loan("L1", "C1", 1000.0, 5.0, 12);
        
        assertEquals("L1", l.getLoanId());
        assertEquals("C1", l.getCustomerId());
        assertEquals(1000.0, l.getPrincipalAmount());
        assertEquals(5.0, l.getInterestRate());
        assertEquals(12, l.getTenureInMonths());
        assertFalse(l.isClosed());
        assertEquals(0.0, l.getTotalAmountPaid());
        
        // Test Repayment Logic
        l.makeRepayment(500.0);
        assertEquals(500.0, l.getTotalAmountPaid());
        assertEquals(500.0, l.getPrincipalAmount());
        
        // Test Closing Logic
        l.makeRepayment(500.0);
        assertEquals(0.0, l.getPrincipalAmount());
        assertTrue(l.isClosed());
        
        assertNotNull(l.toString());
    }

    @Test
    void testSavingsAccount() {
        SavingsAccount sa = new SavingsAccount("S1", "C1", 1000.0);
        assertEquals("SAVINGS", sa.getAccountType());
        assertEquals(0.04, sa.getInterestRate());
        
        // Test Interest Logic
        sa.applyEndOfMonthBenefits();
        // 1000 * (0.04/12) = 3.333 -> 3.33
        assertEquals(1003.33, sa.getBalance());
        
        // Test Withdraw Logic
        assertTrue(sa.canWithdraw(100));
        assertFalse(sa.canWithdraw(2000)); // Too much
        
        assertNotNull(sa.toString());
    }

    @Test
    void testCurrentAccount() {
        CurrentAccount ca = new CurrentAccount("CU1", "C1", 100.0, 50.0);
        assertEquals("CURRENT", ca.getAccountType());
        assertEquals(50.0, ca.getOverdraftLimit());
        
        ca.setOverdraftLimit(100.0);
        assertEquals(100.0, ca.getOverdraftLimit());
        
        // Test Withdraw with Overdraft
        // Balance 100 + Overdraft 100 = 200 Avail
        assertTrue(ca.canWithdraw(190));
        assertFalse(ca.canWithdraw(210));
        
        // Test Fee Logic
        // Simulate negative balance
        // We need to use reflection or a helper since we didn't add setBalance
        // Alternatively, construct it via withdraw if available, or just test the boolean logic
        // For model testing, we just check the methods we can access.
        
        assertNotNull(ca.toString());
    }
}