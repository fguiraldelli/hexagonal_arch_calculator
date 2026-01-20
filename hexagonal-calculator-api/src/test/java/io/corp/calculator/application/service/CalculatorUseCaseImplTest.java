package io.corp.calculator.application.service;

import io.corp.calculator.domain.model.Calculation;
import io.corp.calculator.domain.ports.input.*;
import io.corp.calculator.domain.ports.output.PersistencePort;
import io.corp.calculator.domain.ports.output.TracerPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for CalculatorUseCaseImpl.
 */
@DisplayName("Calculator Use Case Tests")
public class CalculatorUseCaseImplTest {
    
    @Mock
    private TracerPort mockTracerPort;
    
    @Mock
    private PersistencePort mockPersistencePort;
    
    private CalculatorInputPort useCase;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new CalculatorUseCaseImpl(mockTracerPort, mockPersistencePort);
    }
    
    @Test
    @DisplayName("Should perform addition use case")
    public void testPerformAddition() {
        AddCommand command = new AddCommand(
            new BigDecimal("10.50"),
            new BigDecimal("5.25")
        );
        
        CalculationResponse response = useCase.performAddition(command);
        
        assertNotNull(response);
        assertEquals(new BigDecimal("10.50"), response.getOperand1());
        assertEquals(new BigDecimal("5.25"), response.getOperand2());
        assertEquals("+", response.getOperation());
        assertEquals(new BigDecimal("15.75"), response.getResult());
        
        verify(mockTracerPort).trace(any(Calculation.class));
        verify(mockPersistencePort).save(any(Calculation.class));
    }
    
    @Test
    @DisplayName("Should perform subtraction use case")
    public void testPerformSubtraction() {
        SubtractCommand command = new SubtractCommand(
            new BigDecimal("20.50"),
            new BigDecimal("5.25")
        );
        
        CalculationResponse response = useCase.performSubtraction(command);
        
        assertNotNull(response);
        assertEquals(new BigDecimal("15.25"), response.getResult());
        assertEquals("-", response.getOperation());
        
        verify(mockTracerPort).trace(any());
        verify(mockPersistencePort).save(any());
    }
    
    @Test
    @DisplayName("Should perform multiplication use case")
    public void testPerformMultiplication() {
        MultiplyCommand command = new MultiplyCommand(
            new BigDecimal("10.50"),
            new BigDecimal("2.00")
        );
        
        CalculationResponse response = useCase.performMultiplication(command);
        
        assertNotNull(response);
        assertEquals(new BigDecimal("21.00"), response.getResult());
        assertEquals("*", response.getOperation());
        
        verify(mockTracerPort).trace(any());
        verify(mockPersistencePort).save(any());
    }
    
    @Test
    @DisplayName("Should perform division use case")
    public void testPerformDivision() {
        DivideCommand command = new DivideCommand(
            new BigDecimal("10.50"),
            new BigDecimal("2.00"),
            2
        );
        
        CalculationResponse response = useCase.performDivision(command);
        
        assertNotNull(response);
        assertEquals(new BigDecimal("5.25"), response.getResult());
        assertEquals("/", response.getOperation());
        
        verify(mockTracerPort).trace(any());
        verify(mockPersistencePort).save(any());
    }
    
    @Test
    @DisplayName("Should throw exception on division by zero")
    public void testDivisionByZero() {
        DivideCommand command = new DivideCommand(
            new BigDecimal("10"),
            BigDecimal.ZERO,
            2
        );
        
        assertThrows(ArithmeticException.class, () -> {
            useCase.performDivision(command);
        });
    }
}
