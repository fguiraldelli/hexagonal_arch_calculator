package io.corp.calculator.domain.ports.input;

import io.corp.calculator.domain.model.Calculation;

/**
 * Input port - defines contracts for operations coming into the domain.
 */
public interface CalculatorInputPort {
    CalculationResponse performAddition(AddCommand command);
    CalculationResponse performSubtraction(SubtractCommand command);
    CalculationResponse performMultiplication(MultiplyCommand command);
    CalculationResponse performDivision(DivideCommand command);
}
