package com.fincore.model;

import java.io.Serializable;

/**
 * Represents a customer in the banking system.
 */
public class Customer implements Serializable{
    private static final long serialVersionUID = 1L;
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password; // In a real app, this would be hashed
    private int creditScore; // Used for Loan logic
    private double yearlyIncome;

    public Customer(String customerId, String firstName, String lastName, String email, String password, int creditScore, double yearlyIncome) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.creditScore = creditScore;
        this.yearlyIncome = yearlyIncome;
    }

    public String getCustomerId() {
         return customerId; 
    }
    public String getFirstName() {
         return firstName; 
    }
    public String getLastName() {
         return lastName; 
    }
    public String getFullName() {
         return firstName + " " + lastName; 
    }

    public String getEmail() {
         return email; 
    }
    public void setEmail(String email) {
         this.email = email; 
    }

    public String getPhoneNumber() {
         return phoneNumber; 
    }
    public void setPhoneNumber(String phoneNumber) {
         this.phoneNumber = phoneNumber; 
    }

    public boolean verifyPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public int getCreditScore() {
         return creditScore; 
    }
    public void setCreditScore(int creditScore) { 
        if(creditScore >= 300 && creditScore <= 850) {
            this.creditScore = creditScore; 
        }
    }

    public double getYearlyIncome() {
         return yearlyIncome; 
    }

    @Override
    public String toString() {
        return "Customer ID: " + customerId + "\n" +
               "Name: " + getFullName() + "\n" +
               "Email: " + email + "\n" +
               "Credit Score: " + creditScore;
    }
}