package com.fincore.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityLogTest {

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void cleanup() {
        // Optional: Clean up but keep it for debugging if needed
        // File logFile = new File("target/security-events.log");
        // if(logFile.exists()) logFile.delete();
    }

    @Test
    void testSecurityEventLogging() throws Exception {
        // 1. Perform a Malicious Attack (Negative Amount)
        mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("accountId", "ACC-TEST")
                .param("type", "DEPOSIT")
                .param("amount", "-50000")); // Attack

        // 2. Read the Log File
        // The logback-test.xml ensures this file is created
        File logFile = new File("target/security-events.log");
        
        // Wait briefly for IO flush
        int attempts = 0;
        while (!logFile.exists() && attempts < 10) {
            Thread.sleep(100);
            attempts++;
        }

        assertTrue(logFile.exists(), "Log file should be created by logback-test.xml");

        String content = new String(Files.readAllBytes(Paths.get(logFile.toURI())));

        // 3. Verify the System "Noticed" the attack
        assertTrue(content.contains("SECURITY ALERT"), "Log should detect the alert tag");
        assertTrue(content.contains("Suspicious Transaction"), "Log should identify the transaction type");
        assertTrue(content.contains("-50000"), "Log should record the malicious payload");
    }
}