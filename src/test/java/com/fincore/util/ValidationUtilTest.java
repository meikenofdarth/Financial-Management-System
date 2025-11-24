package com.fincore.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    //Email Validation Tests

    @Test
    void testValidEmails() {
        assertTrue(ValidationUtil.isValidEmail("abcd@gmail.com"));
        assertTrue(ValidationUtil.isValidEmail("abcd@a.co.in"));
    }

    @Test
    void testInvalidEmails() {
        assertFalse(ValidationUtil.isValidEmail("abcd"));
        assertFalse(ValidationUtil.isValidEmail("@abcd.com"));
        assertFalse(ValidationUtil.isValidEmail("abcd@.com."));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    //Password Tests

    @Test
    void testValidPasswords() {
        assertTrue(ValidationUtil.isStrongPassword("Abcdefg!"));
        assertTrue(ValidationUtil.isStrongPassword("Testing@123"));
    }

    @Test
    void testInvalidPasswords() {
        assertFalse(ValidationUtil.isStrongPassword("Abcdef!"));
        assertFalse(ValidationUtil.isStrongPassword("abcdefg!")); 
        assertFalse(ValidationUtil.isStrongPassword("Abcdefgh"));
        assertFalse(ValidationUtil.isStrongPassword(null));
    }

    @Test
    void testIsPositiveAmount() {
        assertFalse(ValidationUtil.isPositiveAmount(0)); 
        assertFalse(ValidationUtil.isPositiveAmount(-10));
        assertTrue(ValidationUtil.isPositiveAmount(0.01));
    }
}