package io.corp.calculator.adapter.config;

import io.corp.calculator.domain.ports.output.TracerPort;
import io.corp.calculator.adapter.out.tracer.ConsoleTracerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Adapter Configuration - provides output port implementations.
 */
@Configuration
public class AdapterConfig {
    
    @Bean
    public TracerPort tracerPort() {
        return new ConsoleTracerAdapter();
    }
}
