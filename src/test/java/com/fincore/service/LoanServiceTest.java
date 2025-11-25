package com.fincore.service;

import com.fincore.model.Loan;
import com.fincore.repository.DataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {
    // Tests to verify loan logic
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
    void testApplyForLoanHighCreditScore() {
        // Score >= 750
        accountService.registerCustomer("C1", "Anurag", "R", "a@r.com", "Pass@123", 800, 100000); 
        Loan loan = loanService.applyForLoan("L1", "C1", 50000, 12);
        
        assertNotNull(loan);
        assertEquals(6.5, loan.getInterestRate(), ">=750 credit score should get 6.5% rate");
    }

    @Test
    void testApplyForLoanMidCreditScore() {
        // Score 700-749 
        accountService.registerCustomer("C1", "Anurag", "R", "a@r.com", "Pass@123", 720, 100000);
        
        Loan loan = loanService.applyForLoan("L1", "C1", 50000, 12);
        assertNotNull(loan);
        assertEquals(7.5, loan.getInterestRate(), "700-749 credit score should get 7.5% rate");
    }

    @Test
    void testApplyForLoanLowCreditScore() {
        // Score 600-699
        accountService.registerCustomer("C1", "Anurag", "R", "a@r.com", "Pass@123", 620, 100000);
        
        Loan loan = loanService.applyForLoan("L1", "C1", 50000, 12);
        assertNotNull(loan);
        assertEquals(9.0, loan.getInterestRate(), "600-699 credit score should get 9.0% rate");
    }

    @Test
    void testApplyForLoanRejectedLowCreditScore() {
        // Credit Score < 600 loan rejected
        accountService.registerCustomer("C1", "Anurag", "R", "a@r.com", "Pass@123", 520, 100000);
        assertThrows(IllegalStateException.class, () -> 
            loanService.applyForLoan("L1", "C1", 10000, 12)
        );
    }

    @Test
    void testApplyForLoanRejectedHighAmount() {
        //Loan amount > 5*income rejected
        accountService.registerCustomer("C1", "Anurag", "R", "a@r.com", "Pass@123", 820, 1000);
        
        assertThrows(IllegalStateException.class, () -> 
            loanService.applyForLoan("L1", "C1", 6000, 12)
        );
    }
    
    @Test
    void testApplyForLoaExactBoundary() {
        //Loan amount = 5*income accepted
        accountService.registerCustomer("C1", "Anurag", "R", "a@r.com", "Pass@123", 820, 20000);
        Loan loan = loanService.applyForLoan("L1", "C1", 100000, 12);
        assertNotNull(loan);
        assertThrows(IllegalStateException.class, () -> 
            loanService.applyForLoan("L2", "C1", 100000.01, 12)
        );
    }

    @Test
    void testLoanClosureBehavior() {
        accountService.registerCustomer("C1", "Anurag", "R", "a@r.com", "Pass@123", 720, 1000);
        Loan loan = loanService.applyForLoan("L1", "C1", 1000, 12);
        assertFalse(loan.isClosed());
        loan.makeRepayment(1000);
        assertTrue(loan.isClosed());
    }
    @Test
    void testApplyForLoanBoundaryCreditScore750() {
        accountService.registerCustomer("C1", "Anurag", "R", "a@r.com", "Pass@123", 750, 10000);
        Loan loan = loanService.applyForLoan("L1", "C1", 10000, 12);
        assertEquals(6.5, loan.getInterestRate());
    }

    @Test
    void testApplyForLoanBoundaryCreditScore700() {
        accountService.registerCustomer("C1", "Anurag", "R", "a@r.com", "Pass@123", 700, 10000);   
        Loan loan = loanService.applyForLoan("L1", "C1", 10000, 12);
        assertEquals(7.5, loan.getInterestRate());
    }
    @Test
    void testApplyForLoanBoundaryCreditScore600() {
        accountService.registerCustomer("C1", "Anurag", "R", "a@r.com", "Pass@123", 600, 10000);  
        Loan loan = loanService.applyForLoan("L1", "C1", 10000, 12);
        assertNotNull(loan);
        assertEquals(9.0, loan.getInterestRate());
    }
    @Test
    void testApplyForLoanDataStoreUpdate() {
        accountService.registerCustomer("C1", "Abcd", "Abcd", "ab@cd.com", "Pass@123", 750, 50000);
        loanService.applyForLoan("L1", "C1", 10000, 12);
        Loan storedLoan = dataStore.getLoan("L1");
        assertNotNull(storedLoan);
    }

    @Test
    void testApplyForLoanInvalidCustomer() {
        assertThrows(IllegalArgumentException.class, () -> 
            loanService.applyForLoan("L1", "INVALID_ID", 1000, 12)
        );
    }

    @Test
    void testPayLoanInstallmentInvalidLoanId() {
        assertThrows(IllegalArgumentException.class, () -> 
            loanService.payLoanInstallment("INVALID_ID", 100)
        );
    }
    
    @Test
    void testPayLoanInstallmentAlreadyClosed() {
        accountService.registerCustomer("C67", "C", "67", "c@abcd.com", "Pass@123", 750, 50000);
        loanService.applyForLoan("L67", "C67", 1000, 12);
        
        Loan loan = dataStore.getLoan("L67");
        loan.makeRepayment(2000); //loan repaid
        assertTrue(loan.isClosed());
        assertThrows(IllegalStateException.class, () -> 
            loanService.payLoanInstallment("L67", 100)
        );
    }
}