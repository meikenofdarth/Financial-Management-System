package com.fincore.security;

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
    // Test to verify that security events are logged correctly
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSecurityEventLogging() throws Exception {
        mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("accountId", "ACC-TEST")
                .param("type", "DEPOSIT")
                .param("amount", "-50000"));

        File logFile = new File("target/security-events.log");
        int attempts = 0;
        while (!logFile.exists() && attempts < 10) {//wait for log to be written
            Thread.sleep(100);
            attempts++;
        }
        assertTrue(logFile.exists(), "Log file should be created");
        String content = new String(Files.readAllBytes(Paths.get(logFile.toURI())));
        assertTrue(content.contains("SECURITY ALERT"));
        assertTrue(content.contains("Suspicious Transaction"));
        assertTrue(content.contains("-50000"));
    }
}