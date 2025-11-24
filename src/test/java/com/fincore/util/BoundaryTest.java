package com.fincore.util;

import com.fincore.model.Customer;
import com.fincore.model.SavingsAccount;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoundaryTest {

    @Test
    void testMathBoundaries() {
        assertEquals(0.0, FinancialMath.calculateCompoundInterest(0, 5, 12, 10));
        assertEquals(0.0, FinancialMath.calculateCompoundInterest(1000, 0, 12, 10));
        assertEquals(0.0, FinancialMath.calculateCompoundInterest(1000, 5, 12, 0));
        assertEquals(0.0, FinancialMath.calculateEMI(0, 10, 12));
        assertEquals(100.0, FinancialMath.calculateEMI(1200, 0, 12));
    }

    @Test
    void testValidationBoundaries() {
        assertFalse(ValidationUtil.isPositiveAmount(0.0));
        assertTrue(ValidationUtil.isPositiveAmount(0.0001));
    }
    
    @Test
    void testModelBoundaries() {
        SavingsAccount sa = new SavingsAccount("S1", "C1", 500.0);
        assertTrue(sa.canWithdraw(0));
    
        SavingsAccount sa2 = new SavingsAccount("S2", "C1", 1000.0);
        assertTrue(sa2.canWithdraw(500.0)); 
    }
    
    @Test
    void testCustomerCreditScoreBoundaries() {
        Customer c = new Customer("C", "A", "B", "e", "p", 300, 100);
        
        c.setCreditScore(299); 
        assertEquals(300, c.getCreditScore());
 
        c.setCreditScore(300); 
        assertEquals(300, c.getCreditScore());
        
        c.setCreditScore(851); 
        assertEquals(300, c.getCreditScore());
        
        c.setCreditScore(850);
        assertEquals(850, c.getCreditScore());
    }
}