package io.corp.calculator.adapter.in.web;

import io.corp.calculator.domain.ports.input.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for CalculatorController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Calculator Controller Integration Tests")
public class CalculatorControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CalculatorInputPort calculatorInputPort;
    
    private CalculationResponse mockResponse;
    
    @BeforeEach
    public void setUp() {
        mockResponse = new CalculationResponse(
            new BigDecimal("10.50"),
            new BigDecimal("5.25"),
            "+",
            new BigDecimal("15.75")
        );
    }
    
    @Test
    @DisplayName("Should perform addition via REST API")
    public void testAddEndpoint() throws Exception {
        when(calculatorInputPort.performAddition(any())).thenReturn(mockResponse);
        
        mockMvc.perform(post("/api/v1/calculator/add")
            .param("operand1", "10.50")
            .param("operand2", "5.25"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.operation").value("+"))
            .andExpect(jsonPath("$.result").value("15.75"));
    }
    
    @Test
    @DisplayName("Should return 400 for invalid input")
    public void testAddWithInvalidInput() throws Exception {
        mockMvc.perform(post("/api/v1/calculator/add")
            .param("operand1", "invalid")
            .param("operand2", "5.25"))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should check health endpoint")
    public void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/calculator/health"))
            .andExpect(status().isOk())
            .andExpect(content().string("Calculator API is running"));
    }
}
