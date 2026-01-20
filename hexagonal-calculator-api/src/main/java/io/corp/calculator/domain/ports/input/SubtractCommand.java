package io.corp.calculator.domain.ports.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SubtractCommand {
    private BigDecimal operand1;
    private BigDecimal operand2;
}
