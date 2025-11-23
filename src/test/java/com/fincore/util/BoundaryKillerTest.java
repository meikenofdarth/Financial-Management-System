package com.fincore.util;

import com.fincore.model.Customer;
import com.fincore.model.SavingsAccount;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoundaryKillerTest {

    @Test
    void killMathBoundaries() {
        // Target: if (principal < 0) vs if (principal <= 0)
        // We must test ZERO explicitly.
        
        // 1. Compound Interest
        // Principal = 0 -> Interest = 0
        assertEquals(0.0, FinancialMath.calculateCompoundInterest(0, 5, 12, 10));
        
        // Rate = 0 -> Interest = 0
        assertEquals(0.0, FinancialMath.calculateCompoundInterest(1000, 0, 12, 10));
        
        // Years = 0 -> Interest = 0
        assertEquals(0.0, FinancialMath.calculateCompoundInterest(1000, 5, 12, 0));

        // 2. EMI
        // Principal 0 -> EMI 0
        assertEquals(0.0, FinancialMath.calculateEMI(0, 10, 12));
        
        // Rate 0 -> EMI = Principal / Months
        // 1200 / 12 = 100
        assertEquals(100.0, FinancialMath.calculateEMI(1200, 0, 12));
    }

    @Test
    void killValidationBoundaries() {
        // Target: if (amount > 0) vs if (amount >= 0)
        
        // 0.0 should be FALSE (It is not positive)
        assertFalse(ValidationUtil.isPositiveAmount(0.0));
        
        // 0.0001 should be TRUE
        assertTrue(ValidationUtil.isPositiveAmount(0.0001));
    }
    
    @Test
    void killModelBoundaries() {
        // Target: Savings Account limit >= 500
        SavingsAccount sa = new SavingsAccount("S1", "C1", 500.0);
        
        // 1. Withdraw 0 (Boundary) - Should allow
        assertTrue(sa.canWithdraw(0));
        
        // 2. Withdraw exact balance to leave 500
        // Balance 500. Withdraw 0. Remaining 500. OK.
        
        // Balance 1000. Withdraw 500. Remaining 500. OK.
        SavingsAccount sa2 = new SavingsAccount("S2", "C1", 1000.0);
        assertTrue(sa2.canWithdraw(500.0)); // Exact boundary hit
    }
    
    @Test
    void killCustomerCreditBoundaries() {
        Customer c = new Customer("C", "A", "B", "e", "p", 300, 100);
        
        // Min Score 300
        c.setCreditScore(299); // Should ignore
        assertEquals(300, c.getCreditScore());
        
        c.setCreditScore(300); // Should set (Boundary)
        assertEquals(300, c.getCreditScore());
        
        // Max Score 850
        c.setCreditScore(851); // Should ignore
        assertEquals(300, c.getCreditScore());
        
        c.setCreditScore(850); // Should set
        assertEquals(850, c.getCreditScore());
    }
}