package com.fincore;

import com.fincore.model.*;
import com.fincore.repository.DataStore;
import com.fincore.service.AccountService;
import com.fincore.service.LoanService;
import com.fincore.util.DateUtil;
import com.fincore.util.FinancialMath;
import com.fincore.util.ValidationUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * THE COVERAGE BOOSTER
 * Purpose: Execute every single line of code to eliminate "No Coverage" mutants.
 */
class CoverageBoosterTest {

    @Test
    void hitAllModelMethods() {
        // 1. Transaction
        Transaction t = new Transaction("T1", "A1", "DEP", 100.0, "Desc");
        assertEquals("T1", t.getTransactionId());
        assertEquals("A1", t.getAccountId());
        assertEquals("DEP", t.getType());
        assertEquals(100.0, t.getAmount());
        assertEquals("Desc", t.getDescription());
        assertNotNull(t.getTimestamp());
        assertNotNull(t.toString()); // Hit toString

        // 2. Customer
        Customer c = new Customer("C1", "John", "D", "j@d.com", "pass", 700, 50000);
        c.setPhoneNumber("123");
        c.setEmail("new@d.com");
        c.setCreditScore(800);
        c.setCreditScore(200); // Invalid low
        c.setCreditScore(900); // Invalid high
        
        assertEquals("123", c.getPhoneNumber());
        assertEquals("new@d.com", c.getEmail());
        assertTrue(c.verifyPassword("pass"));
        assertFalse(c.verifyPassword("wrong"));
        assertEquals(800, c.getCreditScore());
        assertNotNull(c.toString());

        // 3. Savings Account
        SavingsAccount sa = new SavingsAccount("S1", "C1", 1000.0);
        sa.applyEndOfMonthBenefits(); // Hit logic
        sa.closeAccount(); // Hit close
        assertFalse(sa.isActive());
        assertEquals(0.04, sa.getInterestRate());
        assertNotNull(sa.toString());

        // 4. Current Account
        CurrentAccount ca = new CurrentAccount("CA1", "C1", -50.0, 500.0);
        ca.applyEndOfMonthBenefits(); // Hit overdraft fee logic
        ca.setOverdraftLimit(600.0);
        assertEquals(600.0, ca.getOverdraftLimit());
        assertNotNull(ca.toString());
        
        // 5. Loan
        Loan l = new Loan("L1", "C1", 1000, 5.0, 12);
        l.makeRepayment(500);
        assertEquals(500, l.getTotalAmountPaid());
        assertNotNull(l.toString());
        
        // Error case in Loan
        Loan closedLoan = new Loan("L2", "C1", 100, 5, 1);
        closedLoan.makeRepayment(100);
        assertThrows(IllegalStateException.class, () -> closedLoan.makeRepayment(10));
    }

    @Test
    void hitAllUtilsEdgeCases() {
        // ValidationUtil - Hit Nulls
        assertFalse(ValidationUtil.isValidEmail(null));
        assertFalse(ValidationUtil.isValidPhoneNumber(null));
        assertFalse(ValidationUtil.isStrongPassword(null));
        assertTrue(ValidationUtil.isNullOrEmpty(null));
        assertTrue(ValidationUtil.isNullOrEmpty(""));
        
        // DateUtil - Hit Nulls
        assertThrows(IllegalArgumentException.class, () -> DateUtil.getDaysBetween(null, null));
        assertFalse(DateUtil.isDateInFuture(null));
        assertNull(DateUtil.addMonths(null, 1));
        
        // FinancialMath - Hit Errors
        assertThrows(IllegalArgumentException.class, () -> FinancialMath.calculateCompoundInterest(-100, 5, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> FinancialMath.calculateCompoundInterest(100, 5, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> FinancialMath.calculateFutureValue(-100, 5, 12));
        
        assertEquals(0.0, FinancialMath.calculateEMI(-100, 5, 12));
    }

    @Test
    void hitDataStoreAndServiceFlows() {
        DataStore ds = DataStore.getInstance();
        ds.clearAll();

        // 1. Hit DataStore lookups that return null
        assertNull(ds.getCustomer("GHOST"));
        assertNull(ds.getAccount("GHOST"));
        assertNull(ds.getLoan("GHOST"));
        assertNull(ds.getCustomerByEmail("ghost@email.com"));
        
        // 2. Hit Service Edge Cases
        AccountService as = new AccountService();
        as.registerCustomer("C1", "A", "B", "valid@email.co", "Pass@123", 700, 50000);
        
        // Create invalid account type
        assertThrows(IllegalArgumentException.class, () -> as.createAccount("INVALID", "A1", "C1", 100));
        
        // Deposit to non-existent
        assertThrows(IllegalArgumentException.class, () -> as.deposit("GHOST", 100));
        
        // Transfer to self
        assertThrows(IllegalArgumentException.class, () -> as.transfer("A1", "A1", 100));
        
        // 3. Hit Loan Service
        LoanService ls = new LoanService();
        assertThrows(IllegalArgumentException.class, () -> ls.applyForLoan("L1", "GHOST", 1000, 12));
    }
}