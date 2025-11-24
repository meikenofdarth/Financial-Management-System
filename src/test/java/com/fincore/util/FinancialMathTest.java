package com.fincore.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class FinancialMathTest {

    //Compound Interest Tests

    @Test
    void testCalculateCompoundInterest() {
        double interest = FinancialMath.calculateCompoundInterest(1000, 5, 1, 1);
        assertEquals(50.00, interest, 0.01, "Interest calculation failed");
    }

    @ParameterizedTest
    @CsvSource({
        "1000, 10, 1, 2, 210.00", 
        "5000, 6, 12, 5, 1744.25"  
    })
    void testCalculateCompoundInterestParameterized(double p, double r, int n, int t, double expected) {
        double result = FinancialMath.calculateCompoundInterest(p, r, n, t);
        assertEquals(expected, result, 0.1, "Interest calculation failed");
    }

    @Test
    void testCalculateCompoundInterestZeroYears() {
        double interest = FinancialMath.calculateCompoundInterest(1000, 5, 1, 0);
        assertEquals(0.00, interest, "Interest calculation failed");
    }

    @Test
    void testCalculateCompoundInterestNegativeValues() {
        assertThrows(IllegalArgumentException.class, () -> 
            FinancialMath.calculateCompoundInterest(-100, 5, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> 
            FinancialMath.calculateCompoundInterest(100, -5, 1, 1));
    }

    //EMI tests

    @Test
    void testCalculateEMI() {
        double emi = FinancialMath.calculateEMI(10000, 10, 12);
        assertEquals(879.16, emi, 0.01);
    }

    @Test
    void testCalculateEMI_ZeroInterest() {
        double emi = FinancialMath.calculateEMI(12000, 0, 12);
        assertEquals(1000.00, emi);
    }
    
    @Test
    void testCalculateEMI_EdgeCases() {
        double emi = FinancialMath.calculateEMI(0, 10, 12);
        assertEquals(0.0, emi);
    }

    //Rounding Tests

    @Test
    void testRound() {
        assertEquals(10.55, FinancialMath.round(10.554));
        assertEquals(10.56, FinancialMath.round(10.556)); 
    }
}