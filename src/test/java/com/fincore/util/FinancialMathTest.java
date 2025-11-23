package com.fincore.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class FinancialMathTest {

    // --- Compound Interest Tests ---

    @Test
    void testCalculateCompoundInterest_StandardCase() {
        // Principal: 1000, Rate: 5%, Times: 1 (Annual), Years: 1
        // Expected: 1000 * (1 + 0.05)^1 = 1050. Interest = 50.
        double interest = FinancialMath.calculateCompoundInterest(1000, 5, 1, 1);
        assertEquals(50.00, interest, 0.01, "Basic annual interest calculation failed");
    }

    @ParameterizedTest
    @CsvSource({
        "1000, 10, 1, 2, 210.00", // 1000 * 1.1^2 = 1210 -> Interest 210
        "5000, 6, 12, 5, 1744.25"  // Complex compounding check
    })
    void testCalculateCompoundInterest_Parameterized(double p, double r, int n, int t, double expected) {
        double result = FinancialMath.calculateCompoundInterest(p, r, n, t);
        assertEquals(expected, result, 0.1, "Parameterized compound interest check failed");
    }

    @Test
    void testCalculateCompoundInterest_ZeroYears() {
        // Mutation Kill: Checks if loop or power logic handles 0 correctly
        double interest = FinancialMath.calculateCompoundInterest(1000, 5, 1, 0);
        assertEquals(0.00, interest, "Interest for 0 years should be 0");
    }

    @Test
    void testCalculateCompoundInterest_NegativeInput() {
        // Mutation Kill: Kills mutants that remove validation checks
        assertThrows(IllegalArgumentException.class, () -> 
            FinancialMath.calculateCompoundInterest(-100, 5, 1, 1));
        
        assertThrows(IllegalArgumentException.class, () -> 
            FinancialMath.calculateCompoundInterest(100, -5, 1, 1));
    }

    // --- EMI Tests ---

    @Test
    void testCalculateEMI_Standard() {
        // P=10000, R=10%, Tenure=12 months
        // EMI should be approx 879.16
        double emi = FinancialMath.calculateEMI(10000, 10, 12);
        assertEquals(879.16, emi, 0.01);
    }

    @Test
    void testCalculateEMI_ZeroInterest() {
        // Boundary Kill: Checks division by zero logic inside formula
        // If rate is 0, EMI = Principal / Months
        double emi = FinancialMath.calculateEMI(12000, 0, 12);
        assertEquals(1000.00, emi);
    }
    
    @Test
    void testCalculateEMI_EdgeCases() {
        // If principal is 0, EMI is 0
        assertEquals(0.0, FinancialMath.calculateEMI(0, 10, 12));
    }

    // --- Rounding Tests ---

    @Test
    void testRound() {
        assertEquals(10.55, FinancialMath.round(10.554));
        assertEquals(10.56, FinancialMath.round(10.556)); // Rounds up
    }
}