package io.corp.calculator.domain.ports.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CalculationResponse {
    private BigDecimal operand1;
    private BigDecimal operand2;
    private String operation;
    private BigDecimal result;
}
