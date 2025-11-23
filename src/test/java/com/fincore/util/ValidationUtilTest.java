package com.fincore.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    // --- Email Tests ---

    @Test
    void testValidEmails() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name+tag@domain.co.in"));
    }

    @Test
    void testInvalidEmails() {
        // Kills regex mutants that might allow missing @ or domain
        assertFalse(ValidationUtil.isValidEmail("plainaddress"));
        assertFalse(ValidationUtil.isValidEmail("@missingusername.com"));
        assertFalse(ValidationUtil.isValidEmail("username@.com."));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    // --- Password Tests (Critical for Mutation Score) ---

    @Test
    void testStrongPassword_Valid() {
        // Has Length(8+), Upper(A), Special(!)
        assertTrue(ValidationUtil.isStrongPassword("Abcdefg!"));
    }

    @Test
    void testStrongPassword_TooShort() {
        // Boundary Analysis: Length 7 (Fail) vs Length 8 (Pass)
        // Kills mutant: if (password.length() <= 8)
        assertFalse(ValidationUtil.isStrongPassword("Abcdef!")); // 7 chars
    }

    @Test
    void testStrongPassword_NoUpper() {
        // Kills mutant: boolean hasUppercase = true;
        assertFalse(ValidationUtil.isStrongPassword("abcdefg!")); 
    }

    @Test
    void testStrongPassword_NoSpecial() {
        // Kills mutant: boolean hasSpecial = true;
        assertFalse(ValidationUtil.isStrongPassword("Abcdefgh"));
    }
    
    @Test
    void testStrongPassword_Null() {
        assertFalse(ValidationUtil.isStrongPassword(null));
    }

    // --- General Validation Tests ---

    @Test
    void testIsPositiveAmount() {
        // Kills mutant: return amount >= 0; (by testing 0)
        assertFalse(ValidationUtil.isPositiveAmount(0)); 
        assertFalse(ValidationUtil.isPositiveAmount(-10));
        assertTrue(ValidationUtil.isPositiveAmount(0.01));
    }
}