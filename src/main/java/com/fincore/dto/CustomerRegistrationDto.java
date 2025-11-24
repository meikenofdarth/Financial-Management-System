package com.fincore.dto;

public class CustomerRegistrationDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int creditScore;
    private double yearlyIncome;

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getCreditScore() { return creditScore; }
    public void setCreditScore(int creditScore) { this.creditScore = creditScore; }
    public double getYearlyIncome() { return yearlyIncome; }
    public void setYearlyIncome(double yearlyIncome) { this.yearlyIncome = yearlyIncome; }
}