package com.fincore.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fincore.service.AccountService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientSideBypassTest {
    // Test to verify that client-side validation bypass attempts are handled correctly
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountService accountService;

    @BeforeEach
    void setupDummyData() {
        try {
            accountService.registerCustomer("C1", "Test", "User", "test@c1.com", "Pass@123", 750, 60000);
            accountService.createAccount("SAVINGS", "A1", "C1", 1000.0);
        } catch (Exception e) {
        }
    }
    @Test
    void bypassHtmlValidationWeakPassword() throws Exception {       
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", "Sanchit")
                .param("lastName", "Kumar")
                .param("email", "s@abcd.com")
                .param("password", "123456") //weak password
                .param("creditScore", "700")
                .param("yearlyIncome", "50000"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void bypassHtmlValidationNegativeDeposit() throws Exception {
        mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("accountId", "A1") 
                .param("type", "DEPOSIT")
                .param("amount", "-1000"))//negative amount
                .andExpect(status().isOk())
                .andExpect(view().name("transaction")) 
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void bypassHtmlValidationInjectionAttempt() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", "<script>alert('hacked')</script>") //injection
                .param("lastName", "Kumar")
                .param("email", "abcdefg") // Invalid Email
                .param("password", "Abcdefg@123")
                .param("creditScore", "700")
                .param("yearlyIncome", "50000"))
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));
    }
    
    @Test
    void bypassHtmlValidationLoan() throws Exception {
        mockMvc.perform(post("/loan")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("customerId", "C1")
                .param("amount", "99999999999999")
                .param("tenureMonths", "12"))
                .andExpect(view().name("loan"))
                .andExpect(model().attributeExists("error"));
    }
}