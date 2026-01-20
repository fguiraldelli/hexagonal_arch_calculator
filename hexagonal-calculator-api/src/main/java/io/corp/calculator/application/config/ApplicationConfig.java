package io.corp.calculator.application.config;

import io.corp.calculator.domain.ports.input.CalculatorInputPort;
import io.corp.calculator.domain.ports.output.PersistencePort;
import io.corp.calculator.domain.ports.output.TracerPort;
import io.corp.calculator.application.service.CalculatorUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application Configuration - wires use cases with output ports.
 */
@Configuration
public class ApplicationConfig {
    
    @Bean
    public CalculatorInputPort calculatorInputPort(
            TracerPort tracerPort,
            PersistencePort persistencePort) {
        return new CalculatorUseCaseImpl(tracerPort, persistencePort);
    }
}
