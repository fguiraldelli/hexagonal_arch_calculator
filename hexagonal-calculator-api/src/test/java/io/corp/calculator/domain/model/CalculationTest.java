package io.corp.calculator.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Calculation domain model.
 */
@DisplayName("Calculation Domain Model Tests")
public class CalculationTest {
    
    @Test
    @DisplayName("Should add two numbers correctly")
    public void testAdd() {
        BigDecimal a = new BigDecimal("10.50");
        BigDecimal b = new BigDecimal("5.25");
        
        Calculation result = Calculation.add(a, b);
        
        assertEquals(a, result.getOperand1());
        assertEquals(b, result.getOperand2());
        assertEquals("+", result.getOperation());
        assertEquals(new BigDecimal("15.75"), result.getResult());
    }
    
    @Test
    @DisplayName("Should subtract two numbers correctly")
    public void testSubtract() {
        BigDecimal a = new BigDecimal("20.50");
        BigDecimal b = new BigDecimal("5.25");
        
        Calculation result = Calculation.subtract(a, b);
        
        assertEquals(a, result.getOperand1());
        assertEquals(b, result.getOperand2());
        assertEquals("-", result.getOperation());
        assertEquals(new BigDecimal("15.25"), result.getResult());
    }
    
    @Test
    @DisplayName("Should multiply two numbers correctly")
    public void testMultiply() {
        BigDecimal a = new BigDecimal("10.50");
        BigDecimal b = new BigDecimal("2.00");
        
        Calculation result = Calculation.multiply(a, b);
        
        assertEquals(a, result.getOperand1());
        assertEquals(b, result.getOperand2());
        assertEquals("*", result.getOperation());
        assertEquals(new BigDecimal("21.00"), result.getResult());
    }
    
    @Test
    @DisplayName("Should divide two numbers correctly")
    public void testDivide() {
        BigDecimal a = new BigDecimal("10.50");
        BigDecimal b = new BigDecimal("2.00");
        
        Calculation result = Calculation.divide(a, b, 2);
        
        assertEquals(a, result.getOperand1());
        assertEquals(b, result.getOperand2());
        assertEquals("/", result.getOperation());
        assertEquals(new BigDecimal("5.25"), result.getResult());
    }
    
    @Test
    @DisplayName("Should throw exception when adding null operand")
    public void testAddWithNullOperand() {
        assertThrows(IllegalArgumentException.class, () -> {
            Calculation.add(null, new BigDecimal("5"));
        });
    }
    
    @Test
    @DisplayName("Should throw exception when dividing by zero")
    public void testDivideByZero() {
        assertThrows(ArithmeticException.class, () -> {
            Calculation.divide(new BigDecimal("10"), BigDecimal.ZERO, 2);
        });
    }
    
    @Test
    @DisplayName("Should handle precise decimal calculations")
    public void testPreciseDecimalCalculation() {
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.2");
        
        Calculation result = Calculation.add(a, b);
        
        assertEquals(new BigDecimal("0.3"), result.getResult());
    }
}
