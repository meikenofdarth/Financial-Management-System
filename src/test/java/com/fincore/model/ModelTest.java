package com.fincore.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void testCustomerFullCoverage() {
        Customer c = new Customer("C1", "John", "Doe", "j@d.com", "pass", 700, 50000);
        
        // Exercise every getter
        assertEquals("C1", c.getCustomerId());
        assertEquals("John", c.getFirstName());
        assertEquals("Doe", c.getLastName());
        assertEquals("John Doe", c.getFullName());
        assertEquals("j@d.com", c.getEmail());
        assertTrue(c.verifyPassword("pass"));
        assertEquals(700, c.getCreditScore());
        assertEquals(50000, c.getYearlyIncome());
        
        // Exercise Setters
        c.setEmail("new@test.com");
        c.setPhoneNumber("555-0000");
        c.setCreditScore(800);
        
        // Validating Setters worked
        assertEquals("new@test.com", c.getEmail());
        assertEquals("555-0000", c.getPhoneNumber());
        assertEquals(800, c.getCreditScore());
        
        // CRITICAL: Call toString() to kill String Mutants
        String str = c.toString();
        assertNotNull(str);
        assertTrue(str.contains("C1"));
    }

    @Test
    void testAccountFullCoverage() {
        // Savings
        SavingsAccount sa = new SavingsAccount("S1", "C1", 1000.0);
        assertEquals("S1", sa.getAccountId());
        assertEquals("C1", sa.getCustomerId());
        assertTrue(sa.isActive());
        assertEquals("SAVINGS", sa.getAccountType());
        assertEquals(0.04, sa.getInterestRate());
        
        // Helper methods
        sa.closeAccount();
        assertFalse(sa.isActive());
        
        // toString
        String saStr = sa.toString();
        assertTrue(saStr.contains("S1"));
        assertTrue(saStr.contains("Closed"));

        // Current
        CurrentAccount ca = new CurrentAccount("CU1", "C1", 0.0, 500.0);
        assertEquals("CURRENT", ca.getAccountType());
        ca.setOverdraftLimit(600.0);
        assertEquals(600.0, ca.getOverdraftLimit());
        
        // toString
        String caStr = ca.toString();
        assertTrue(caStr.contains("CU1"));
    }

    @Test
    void testLoanFullCoverage() {
        Loan l = new Loan("L1", "C1", 1000.0, 5.0, 12);
        
        assertEquals("L1", l.getLoanId());
        assertEquals("C1", l.getCustomerId());
        assertEquals(1000.0, l.getPrincipalAmount());
        assertEquals(5.0, l.getInterestRate());
        assertEquals(12, l.getTenureInMonths());
        assertEquals(0.0, l.getTotalAmountPaid());
        
        // toString
        String lStr = l.toString();
        assertTrue(lStr.contains("L1"));
        assertTrue(lStr.contains("ACTIVE"));
    }

    @Test
    void testTransactionFullCoverage() {
        Transaction t = new Transaction("T1", "A1", "DEP", 100.0, "Desc");
        
        assertEquals("T1", t.getTransactionId());
        assertEquals("A1", t.getAccountId());
        assertEquals("DEP", t.getType());
        assertEquals(100.0, t.getAmount());
        assertEquals("Desc", t.getDescription());
        assertNotNull(t.getTimestamp());
        
        String tStr = t.toString();
        assertTrue(tStr.contains("T1"));
        assertTrue(tStr.contains("Desc"));
    }
}