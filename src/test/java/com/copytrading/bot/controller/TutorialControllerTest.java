package com.copytrading.bot.controller;


import com.copytrading.bot.model.Bybit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TutorialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Bybit bybit;

    @Test
    public void testAvailableBalance() throws Exception {
        // Set up the mock response from Bybit class
        when(bybit.availableBalance(any(), any(), any())).thenReturn(1000.0);

        // Test the balance operation
        mockMvc.perform(get("/tutorial")
                        .param("operation_type", "balance")
                        .param("api_key", "test_api_key")
                        .param("api_secret", "test_api_secret")
                        .param("coin", "USDT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("balance").value(1000.0));
    }

    // You can add more test methods for other operation types here
}
