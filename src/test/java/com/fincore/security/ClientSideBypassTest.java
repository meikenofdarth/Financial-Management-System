package com.fincore.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientSideBypassTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void bypassHtmlValidation_WeakPassword() throws Exception {
        // The HTML form might say "Password must be strong", but we send "123" directly.
        // The Backend Logic (AccountService) MUST catch this.
        
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", "Hacker")
                .param("lastName", "One")
                .param("email", "hacker@test.com")
                .param("password", "123") // WEAK PASSWORD
                .param("creditScore", "700")
                .param("yearlyIncome", "50000"))
                .andExpect(status().isOk()) // Should not crash (500 error)
                .andExpect(view().name("register")) // Should kick us back to register page
                .andExpect(model().attributeExists("error")); // Must contain an error message
    }

    @Test
    void bypassHtmlValidation_NegativeDeposit() throws Exception {
        // The HTML input likely has type="number" min="0". 
        // We bypass that and send -1000.
        
        mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("accountId", "ACC-ANY") // Even if ID is wrong, neg amount is the check
                .param("type", "DEPOSIT")
                .param("amount", "-1000")) // NEGATIVE AMOUNT
                .andExpect(status().isOk())
                .andExpect(view().name("transaction")) // Should return to form
                .andExpect(model().attributeExists("error")); // Backend must reject it
    }

    @Test
    void bypassHtmlValidation_SQLInjectionAttempt() throws Exception {
        // Attempting to inject scripts into the name field.
        // The backend should accept it as text but handle it safely, 
        // OR ValidationUtil should reject special chars if you added that logic.
        
        // Assuming your ValidationUtil accepts names but maybe fails on email format
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", "<script>alert('hacked')</script>") 
                .param("lastName", "Doe")
                .param("email", "bad-email-format") // Invalid Email
                .param("password", "Strong@123")
                .param("creditScore", "700")
                .param("yearlyIncome", "50000"))
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));
    }
    
    @Test
    void bypassHtmlValidation_HugeLoan() throws Exception {
        // HTML might limit input length, but we send a massive number
        // LoanService logic (amount > 5x income) should catch this.
        
        mockMvc.perform(post("/loan")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("customerId", "C1")
                .param("amount", "99999999999999") // Huge amount
                .param("tenureMonths", "12"))
                .andExpect(view().name("loan"))
                .andExpect(model().attributeExists("error"));
    }
}