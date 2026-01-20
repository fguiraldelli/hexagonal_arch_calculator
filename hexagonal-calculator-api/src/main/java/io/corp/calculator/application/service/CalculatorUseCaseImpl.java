package io.corp.calculator.application.service;

import io.corp.calculator.domain.model.Calculation;
import io.corp.calculator.domain.ports.input.*;
import io.corp.calculator.domain.ports.output.PersistencePort;
import io.corp.calculator.domain.ports.output.TracerPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

/**
 * Use Case Implementation: Arithmetic operations.
 * Orchestrates domain logic and uses output ports for side effects.
 */
@Slf4j
@RequiredArgsConstructor
public class CalculatorUseCaseImpl implements CalculatorInputPort {
    
    private final TracerPort tracerPort;
    private final PersistencePort persistencePort;
    
    @Override
    public CalculationResponse performAddition(AddCommand command) {
        log.info("Performing addition: {} + {}", command.getOperand1(), command.getOperand2());
        
        Calculation calculation = Calculation.add(
            command.getOperand1(),
            command.getOperand2()
        );
        
        tracerPort.trace(calculation);
        persistencePort.save(calculation);
        
        return toResponse(calculation);
    }
    
    @Override
    public CalculationResponse performSubtraction(SubtractCommand command) {
        log.info("Performing subtraction: {} - {}", command.getOperand1(), command.getOperand2());
        
        Calculation calculation = Calculation.subtract(
            command.getOperand1(),
            command.getOperand2()
        );
        
        tracerPort.trace(calculation);
        persistencePort.save(calculation);
        
        return toResponse(calculation);
    }
    
    @Override
    public CalculationResponse performMultiplication(MultiplyCommand command) {
        log.info("Performing multiplication: {} * {}", command.getOperand1(), command.getOperand2());
        
        Calculation calculation = Calculation.multiply(
            command.getOperand1(),
            command.getOperand2()
        );
        
        tracerPort.trace(calculation);
        persistencePort.save(calculation);
        
        return toResponse(calculation);
    }
    
    @Override
    public CalculationResponse performDivision(DivideCommand command) {
        log.info("Performing division: {} / {} (scale: {})", 
            command.getOperand1(), command.getOperand2(), command.getScale());
        
        Calculation calculation = Calculation.divide(
            command.getOperand1(),
            command.getOperand2(),
            command.getScale()
        );
        
        tracerPort.trace(calculation);
        persistencePort.save(calculation);
        
        return toResponse(calculation);
    }
    
    private CalculationResponse toResponse(Calculation calculation) {
        return new CalculationResponse(
            calculation.getOperand1(),
            calculation.getOperand2(),
            calculation.getOperation(),
            calculation.getResult()
        );
    }
}
