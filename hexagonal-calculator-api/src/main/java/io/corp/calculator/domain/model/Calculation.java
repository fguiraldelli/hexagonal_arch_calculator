package io.corp.calculator.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Domain model representing a calculation operation.
 * Contains pure business logic for arithmetic operations.
 */
@Data
@AllArgsConstructor
public class Calculation {
    
    private BigDecimal operand1;
    private BigDecimal operand2;
    private String operation;
    private BigDecimal result;
    
    // Business logic methods
    public static Calculation add(BigDecimal operand1, BigDecimal operand2) {
        if (operand1 == null || operand2 == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }
        BigDecimal result = operand1.add(operand2);
        return new Calculation(operand1, operand2, "+", result);
    }
    
    public static Calculation subtract(BigDecimal operand1, BigDecimal operand2) {
        if (operand1 == null || operand2 == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }
        BigDecimal result = operand1.subtract(operand2);
        return new Calculation(operand1, operand2, "-", result);
    }
    
    public static Calculation multiply(BigDecimal operand1, BigDecimal operand2) {
        if (operand1 == null || operand2 == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }
        BigDecimal result = operand1.multiply(operand2);
        return new Calculation(operand1, operand2, "*", result);
    }
    
    public static Calculation divide(BigDecimal operand1, BigDecimal operand2, int scale) {
        if (operand1 == null || operand2 == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }
        if (operand2.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        BigDecimal result = operand1.divide(operand2, scale, RoundingMode.HALF_UP);
        return new Calculation(operand1, operand2, "/", result);
    }
}
