package io.corp.calculator.domain.ports.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DivideCommand {
    private BigDecimal operand1;
    private BigDecimal operand2;
    private int scale;
}
