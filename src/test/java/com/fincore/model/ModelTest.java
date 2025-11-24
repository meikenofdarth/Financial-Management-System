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

// ... existing tests ...

    @Test
    void testSavingsAccount_WithdrawBoundaries() {
        // Min Balance is 500.00
        SavingsAccount sa = new SavingsAccount("S1", "C1", 1000.0);

        // 1. Test Safe Withdrawal
        assertTrue(sa.canWithdraw(400.0));

        // 2. Test EXACT Boundary (Killing ConditionalsBoundaryMutator)
        // 1000 - 500 = 500. Logic is >= 500.
        // Mutant changes >= to >. This assertion kills it.
        assertTrue(sa.canWithdraw(500.0), "Should allow withdrawing down to exactly min balance");

        // 3. Test Just Over Boundary
        // 1000 - 500.01 = 499.99. Should fail.
        assertFalse(sa.canWithdraw(500.01), "Should not allow withdrawing below min balance");
    }

    @Test
    void testSavingsAccount_InactiveWithdraw() {
        SavingsAccount sa = new SavingsAccount("S1", "C1", 1000.0);
        sa.closeAccount();

        // Killing NegateConditionalsMutator on "this.isActive"
        // Even with enough balance, a closed account cannot withdraw
        assertFalse(sa.canWithdraw(100.0));
    }

    @Test
    void testSavingsAccount_InterestCalculation() {
        // Setup: 1200 Balance. Rate is 0.04 (4%).
        // Math: 1200 * (0.04 / 12) = 4.00 interest.
        SavingsAccount sa = new SavingsAccount("S1", "C1", 1200.0);
        int initialHistory = sa.getTransactionHistory().size();

        sa.applyEndOfMonthBenefits();

        // Killing MathMutator: Verify exact calculation
        assertEquals(1204.00, sa.getBalance(), 0.001, "Interest calculation must be precise");

        // Killing VoidMethodCallMutator: Verify transaction was added
        assertEquals(initialHistory + 1, sa.getTransactionHistory().size(), "Interest application must record a transaction");
        assertEquals("INTEREST", sa.getTransactionHistory().get(initialHistory).getType());
    }

    @Test
    void testSavingsAccount_InterestEdgeCases() {
        // 1. Inactive Account (Killing NegateConditionalsMutator on !isActive)
        SavingsAccount saClosed = new SavingsAccount("S2", "C1", 1200.0);
        saClosed.closeAccount();
        saClosed.applyEndOfMonthBenefits();
        assertEquals(1200.0, saClosed.getBalance(), "Closed account should not get interest");

        // 2. Zero Balance (Killing ConditionalsBoundaryMutator on balance > 0)
        SavingsAccount saZero = new SavingsAccount("S3", "C1", 0.0);
        saZero.applyEndOfMonthBenefits();
        assertEquals(0.0, saZero.getBalance());
        
        // 3. Negative Balance (Killing NegateConditionalsMutator)
        // (Assuming we force a negative balance via constructor hack or logic bypass)
        SavingsAccount saNeg = new SavingsAccount("S4", "C1", -100.0);
        saNeg.applyEndOfMonthBenefits();
        assertEquals(-100.0, saNeg.getBalance(), "Negative balance should not get interest");
    }
    @Test
    void testCurrentAccount_WithdrawBoundaries() {
        // Setup: Balance 1000.0, Overdraft Limit 500.0.
        // Total Buying Power = 1500.0
        CurrentAccount ca = new CurrentAccount("C1", "U1", 1000.0, 500.0);

        // 1. Test Safe Withdrawal (Within Balance)
        assertTrue(ca.canWithdraw(900.0));

        // 2. Test Withdrawal using Overdraft (Killing MathMutator on + vs -)
        // Logic: Balance + Limit (1000 + 500 = 1500).
        // Mutant: Balance - Limit (1000 - 500 = 500).
        // If we withdraw 600, Original passes, Mutant fails.
        assertTrue(ca.canWithdraw(600.0), "Should allow withdrawal using overdraft portion");

        // 3. Test EXACT Boundary (Killing ConditionalsBoundaryMutator)
        // Total Power: 1500. Logic is >= amount.
        // Mutant changes >= to >. This assertion kills it.
        assertTrue(ca.canWithdraw(1500.0), "Should allow withdrawing exactly (Balance + Overdraft)");

        // 4. Test Just Over Boundary
        // 1500.01 should fail.
        assertFalse(ca.canWithdraw(1500.01), "Should not allow withdrawing beyond overdraft limit");
    }

    @Test
    void testCurrentAccount_InactiveWithdraw() {
        CurrentAccount ca = new CurrentAccount("C1", "U1", 1000.0, 500.0);
        ca.closeAccount();

        // Killing NegateConditionalsMutator on "this.isActive"
        // Even with huge overdraft limit, a closed account cannot withdraw
        assertFalse(ca.canWithdraw(100.0));
    }

    @Test
    void testCurrentAccount_EndOfMonth_ApplyFee() {
        // Setup: Negative Balance (-100). Logic: if (balance < 0).
        CurrentAccount ca = new CurrentAccount("C1", "U1", -100.0, 500.0);
        int initialHistory = ca.getTransactionHistory().size();

        ca.applyEndOfMonthBenefits();

        // 1. Verify Fee Calculation (Killing MathMutator)
        // -100 - 25 = -125.
        assertEquals(-125.0, ca.getBalance(), 0.001, "Fee of 25.00 should be subtracted from negative balance");

        // 2. Verify Transaction (Killing VoidMethodCallMutator)
        assertEquals(initialHistory + 1, ca.getTransactionHistory().size(), "Fee application must record a transaction");
        Transaction t = ca.getTransactionHistory().get(initialHistory);
        assertEquals("FEE", t.getType());
        assertEquals(25.0, t.getAmount(), 0.001);
    }

    @Test
    void testCurrentAccount_EndOfMonth_FeeBoundary() {
        // 1. Positive Balance (No Fee)
        CurrentAccount caPos = new CurrentAccount("C2", "U1", 100.0, 500.0);
        caPos.applyEndOfMonthBenefits();
        assertEquals(100.0, caPos.getBalance(), "Positive balance should not incur fee");

        // 2. EXACT Zero Balance (Killing ConditionalsBoundaryMutator)
        // Logic is: if (balance < 0).
        // Mutant changes < to <=.
        // If balance is 0.0: Original (False) -> No Fee. Mutant (True) -> Fee applied.
        CurrentAccount caZero = new CurrentAccount("C3", "U1", 0.0, 500.0);
        caZero.applyEndOfMonthBenefits();
        assertEquals(0.0, caZero.getBalance(), "Zero balance should not incur overdraft fee");
    }

    @Test
    void testCurrentAccount_SettersAndOverdraftLogic() {
        CurrentAccount ca = new CurrentAccount("C4", "U1", 0.0, 0.0);
        
        // Verify Setter works (Killing VoidMethodCallMutator if setter is empty)
        ca.setOverdraftLimit(200.0);
        assertEquals(200.0, ca.getOverdraftLimit(), 0.001);
        
        // Verify new limit is actually used in logic
        assertTrue(ca.canWithdraw(150.0));
        assertFalse(ca.canWithdraw(250.0));
    }

    @Test
    void testCurrentAccount_ToString() {
        // Good for coverage, ensures values are mapped correctly
        CurrentAccount ca = new CurrentAccount("ACC-99", "CUST-1", 500.50, 200.00);
        String result = ca.toString();
        
        assertTrue(result.contains("ACC-99"));
        assertTrue(result.contains("500.50"));
        assertTrue(result.contains("200.00"));
        assertTrue(result.contains("Current Account"));
    }
}