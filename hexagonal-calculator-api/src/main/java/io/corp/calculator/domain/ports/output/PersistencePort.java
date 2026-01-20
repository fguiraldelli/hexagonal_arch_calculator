package io.corp.calculator.domain.ports.output;

import io.corp.calculator.domain.model.Calculation;
import java.util.Optional;

/**
 * Output port - defines contract for persistence operations.
 */
public interface PersistencePort {
    void save(Calculation calculation);
    Optional<Calculation> findById(String id);
}
