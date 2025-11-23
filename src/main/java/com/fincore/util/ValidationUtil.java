package com.fincore.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ValidationUtil {

    // Regex for basic email format
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    
    // Regex for phone (simple version: 10 digits)
    private static final String PHONE_REGEX = "^\\d{10}$";

    /**
     * Validates if the email is in correct format.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Validates if the phone number is valid.
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false;
        return phone.matches(PHONE_REGEX);
    }

    /**
     * Validates password strength.
     * Rules:
     * 1. At least 8 characters long
     * 2. Contains at least one Uppercase letter
     * 3. Contains at least one Special character (!@#$%^&*)
     */
    public static boolean isStrongPassword(String password) {
        if (password == null) return false;

        // Check 1: Length
        if (password.length() < 8) {
            return false;
        }

        // Check 2: Uppercase
        boolean hasUppercase = !password.equals(password.toLowerCase());
        
        // Check 3: Special Char
        // We iterate specifically to create loops and logic for mutation testing
        boolean hasSpecial = false;
        String specialChars = "!@#$%^&*()-_=+[]{}|;:,.<>?";
        
        for (char c : password.toCharArray()) {
            if (specialChars.contains(String.valueOf(c))) {
                hasSpecial = true;
                break;
            }
        }

        // Logic combining multiple booleans
        return hasUppercase && hasSpecial;
    }

    /**
     * Checks if a string is null or empty.
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Validates if an amount is positive.
     */
    public static boolean isPositiveAmount(double amount) {
        // Mutation target: Change > to >=
        return amount > 0;
    }
}