package com.fincore.util;

public class FinancialMath {

    /**
     * Calculates Compound Interest.
     * Formula: A = P(1 + r/n)^(nt)
     * @param principal The initial amount (P)
     * @param annualRate The annual interest rate in percentage (e.g., 5.0 for 5%)
     * @param timesCompoundedPerYear Number of times interest is compounded per year (n)
     * @param years Number of years (t)
     * @return The amount of interest earned.
     */
    public static double calculateCompoundInterest(double principal, double annualRate, int timesCompoundedPerYear, int years) {
        if (principal < 0 || annualRate < 0 || years < 0) {
            throw new IllegalArgumentException("Inputs cannot be negative");
        }
        if (timesCompoundedPerYear <= 0) {
            throw new IllegalArgumentException("Compounding frequency must be positive");
        }

        double decimalRate = annualRate / 100.0;
        double body = 1 + (decimalRate / timesCompoundedPerYear);
        double exponent = timesCompoundedPerYear * years;
        
        double totalAmount = principal * Math.pow(body, exponent);
        
        // Return only the interest component
        return round(totalAmount - principal);
    }

    /**
     * Calculates Equated Monthly Installment (EMI) for loans.
     * Formula: E = P * r * (1+r)^n / ((1+r)^n - 1)
     * @param principal Loan amount
     * @param annualInterestRate Annual interest rate in percentage
     * @param tenureInMonths Loan tenure in months
     * @return Monthly EMI amount
     */
    public static double calculateEMI(double principal, double annualInterestRate, int tenureInMonths) {
        if (principal <= 0 || tenureInMonths <= 0) return 0.0;
        if (annualInterestRate == 0) return round(principal / tenureInMonths);

        double monthlyRate = annualInterestRate / (12 * 100); // Convert annual % to monthly decimal
        
        double numerator = principal * monthlyRate * Math.pow(1 + monthlyRate, tenureInMonths);
        double denominator = Math.pow(1 + monthlyRate, tenureInMonths) - 1;

        return round(numerator / denominator);
    }

    /**
     * Calculates Future Value of a series of regular payments (e.g., Recurring Deposit).
     * Formula: FV = P * ((1+r)^n - 1) / r
     * @param monthlyPayment The amount deposited every month
     * @param annualRate Annual interest rate
     * @param months Total months
     * @return Future Value
     */
    public static double calculateFutureValue(double monthlyPayment, double annualRate, int months) {
        if (monthlyPayment < 0) throw new IllegalArgumentException("Payment cannot be negative");

        double monthlyRate = annualRate / (12 * 100);
        
        double body = Math.pow(1 + monthlyRate, months) - 1;
        double result = monthlyPayment * (body / monthlyRate);
        
        return round(result);
    }

    /**
     * Helper to round double values to 2 decimal places.
     */
    public static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}