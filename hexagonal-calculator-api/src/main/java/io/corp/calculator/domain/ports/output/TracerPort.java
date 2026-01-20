package io.corp.calculator.domain.ports.output;

/**
 * Output port - defines contract for external tracer system.
 */
public interface TracerPort {
    <T> void trace(T result);
}
