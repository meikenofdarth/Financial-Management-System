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
}