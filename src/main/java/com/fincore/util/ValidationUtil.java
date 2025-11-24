package com.fincore.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ValidationUtil {

    // FIXED REGEX: Enforces a domain dot and at least 2 letters at the end (e.g., .com)
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
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
        boolean hasSpecial = false;
        String specialChars = "!@#$%^&*()-_=+[]{}|;:,.<>?";
        
        for (char c : password.toCharArray()) {
            if (specialChars.contains(String.valueOf(c))) {
                hasSpecial = true;
                break;
            }
        }

        return hasUppercase && hasSpecial;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static boolean isPositiveAmount(double amount) {
        return amount > 0;
    }
}