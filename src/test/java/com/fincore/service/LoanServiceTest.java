package com.fincore.service;

import com.fincore.model.Customer;
import com.fincore.model.Loan;
import com.fincore.repository.DataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {

    private LoanService loanService;
    private AccountService accountService;
    private DataStore dataStore;

    @BeforeEach
    void setUp() {
        dataStore = DataStore.getInstance();
        dataStore.clearAll();
        loanService = new LoanService();
        accountService = new AccountService();
    }
    
    @Test
    void testApplyForLoan_Success_PrimeRate() {
        // Score 750+ gets 6.5% interest
        accountService.registerCustomer("C1", "Jane", "Doe", "jane@test.com", "Pass@123", 800, 100000);
        
        Loan loan = loanService.applyForLoan("L1", "C1", 50000, 12);
        
        assertNotNull(loan);
        assertEquals(6.5, loan.getInterestRate(), "Prime customers should get 6.5% rate");
    }

    @Test
    void testApplyForLoan_Success_MidRate() {
        // Score 700-749 gets 7.5% interest
        accountService.registerCustomer("C2", "Mid", "User", "mid@test.com", "Pass@123", 720, 100000);
        
        Loan loan = loanService.applyForLoan("L2", "C2", 50000, 12);
        
        assertEquals(7.5, loan.getInterestRate(), "Mid-tier customers should get 7.5% rate");
    }

    @Test
    void testApplyForLoan_Success_LowRate() {
        // Score 600-699 gets 9.0% interest
        accountService.registerCustomer("C3", "Low", "User", "low@test.com", "Pass@123", 650, 100000);
        
        Loan loan = loanService.applyForLoan("L3", "C3", 50000, 12);
        
        assertEquals(9.0, loan.getInterestRate(), "Low-tier customers should get 9.0% rate");
    }

    @Test
    void testApplyForLoan_Rejected_LowScore() {
        // Score < 600 rejected
        accountService.registerCustomer("C4", "Fail", "User", "fail@test.com", "Pass@123", 500, 100000);
        
        assertThrows(IllegalStateException.class, () -> 
            loanService.applyForLoan("L4", "C4", 10000, 12)
        );
    }

    @Test
    void testApplyForLoan_Rejected_HighAmount() {
        // Income 1000 * 5 = 5000 Max. Requesting 6000.
        accountService.registerCustomer("C5", "Greedy", "User", "greedy@test.com", "Pass@123", 750, 1000);
        
        assertThrows(IllegalStateException.class, () -> 
            loanService.applyForLoan("L5", "C5", 6000, 12)
        );
    }
    
    @Test
    void testApplyForLoan_ExactBoundary_MaxIncome() {
        // Income = 20,000. Max Loan (x5) = 100,000.
        accountService.registerCustomer("C99", "Edge", "Case", "edge@test.com", "Pass@123", 800, 20000);
        
        // 1. Try exactly 100,000 (Should Pass)
        // This kills the mutant that changes "amount > max" to "amount >= max"
        Loan loan = loanService.applyForLoan("L99", "C99", 100000, 12);
        assertNotNull(loan);
        
        // 2. Try 100,001 (Should Fail)
        assertThrows(IllegalStateException.class, () -> 
            loanService.applyForLoan("L100", "C99", 100000.01, 12)
        );
    }
    @Test
void testLoanClosureBehavior() {
    // Register valid customer
    accountService.registerCustomer(
        "C10", "John", "Doe", "john@test.com",
        "Pass@123", 750, 50000
    );

    // Apply for loan
    Loan loan = loanService.applyForLoan("L10", "C10", 1000, 12);

    // At start: loan should NOT be closed
    assertFalse(loan.isClosed(), "Loan should start as active");

    // Repay full amount to close the loan
    loan.makeRepayment(1000);

    // After paying: loan must be marked closed
    assertTrue(loan.isClosed(), "Loan should be closed after full repayment");
}
@Test
    void testApplyForLoan_Boundary_Score750() {
        // Logic: if (score >= 750) -> 6.5%. 
        // Mutant: if (score > 750).
        // If score is 750: Original=True (6.5). Mutant=False (Fall through to 7.5).
        accountService.registerCustomer("C_Prime", "Test", "User", "p@t.com", "Pass@123", 750, 50000);
        
        Loan loan = loanService.applyForLoan("L_Prime", "C_Prime", 10000, 12);
        
        assertEquals(6.5, loan.getInterestRate(), "Score 750 exactly should still get 6.5%");
    }

    @Test
    void testApplyForLoan_Boundary_Score700() {
        // Logic: else if (score >= 700) -> 7.5%.
        // Mutant: else if (score > 700).
        // If score is 700: Original=True (7.5). Mutant=False (Fall through to 9.0).
        accountService.registerCustomer("C_Mid", "Test", "User", "m@t.com", "Pass@123", 700, 50000);
        
        Loan loan = loanService.applyForLoan("L_Mid", "C_Mid", 10000, 12);
        
        assertEquals(7.5, loan.getInterestRate(), "Score 700 exactly should get 7.5%");
    }
    @Test
    void testApplyForLoan_Boundary_Score600() {
        // Logic: if (score < 600) throw.
        // Mutant: if (score <= 600) throw.
        // If score is 600: Original=False (Safe). Mutant=True (Throws Exception).
        accountService.registerCustomer("C_Low", "Test", "User", "l@t.com", "Pass@123", 600, 50000);
        
        Loan loan = loanService.applyForLoan("L_Low", "C_Low", 10000, 12);
        
        assertNotNull(loan);
        assertEquals(9.0, loan.getInterestRate());
    }
    @Test
    void testApplyForLoan_SideEffect_DataStoreUpdate() {
        accountService.registerCustomer("C_Store", "Test", "User", "s@t.com", "Pass@123", 750, 50000);
        
        loanService.applyForLoan("L_Store", "C_Store", 10000, 12);
        
        // Kill VoidMethodCallMutator on dataStore.addLoan(loan)
        Loan storedLoan = dataStore.getLoan("L_Store");
        assertNotNull(storedLoan, "Loan must be persisted in DataStore");
    }
    @Test
    void testApplyForLoan_InvalidCustomer() {
        // Kill NullReturn or Conditional Negation on (customer == null)
        assertThrows(IllegalArgumentException.class, () -> 
            loanService.applyForLoan("L_X", "NON_EXISTENT_ID", 1000, 12)
        );
    }

    @Test
    void testPayLoanInstallment_InvalidLoanId() {
        // Kill (loan == null) check removal
        assertThrows(IllegalArgumentException.class, () -> 
            loanService.payLoanInstallment("INVALID_ID", 100)
        );
    }

    @Test
    void testPayLoanInstallment_AlreadyClosed() {
        accountService.registerCustomer("C_Closed", "C", "User", "c@t.com", "Pass@123", 750, 50000);
        loanService.applyForLoan("L_Closed", "C_Closed", 1000, 12);
        
        // Close it manually
        Loan loan = dataStore.getLoan("L_Closed");
        loan.makeRepayment(2000); // Overpay to close
        assertTrue(loan.isClosed());
        
        // Kill NegateConditionalsMutator on if(loan.isClosed())
        assertThrows(IllegalStateException.class, () -> 
            loanService.payLoanInstallment("L_Closed", 100)
        );
    }
}