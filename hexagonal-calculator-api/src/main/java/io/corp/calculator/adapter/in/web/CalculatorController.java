package io.corp.calculator.adapter.in.web;

import io.corp.calculator.domain.ports.input.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

/**
 * REST Input Adapter - translates HTTP requests to domain commands.
 */
@RestController
@RequestMapping("/api/v1/calculator")
@RequiredArgsConstructor
@Tag(name = "Calculator", description = "Arithmetic operations with BigDecimal precision")
public class CalculatorController {
    
    private final CalculatorInputPort calculatorInputPort;
    
    @PostMapping("/add")
    @Operation(summary = "Add two numbers", description = "Performs addition with BigDecimal precision")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operation successful",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = CalculationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CalculationResponse> add(
            @Parameter(description = "First operand", example = "10.50", required = true)
            @RequestParam String operand1,
            @Parameter(description = "Second operand", example = "5.25", required = true)
            @RequestParam String operand2) {
        try {
            AddCommand command = new AddCommand(
                new BigDecimal(operand1),
                new BigDecimal(operand2)
            );
            CalculationResponse response = calculatorInputPort.performAddition(command);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/subtract")
    @Operation(summary = "Subtract two numbers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operation successful",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = CalculationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CalculationResponse> subtract(
            @Parameter(description = "Operand 1", example = "20.50", required = true)
            @RequestParam String operand1,
            @Parameter(description = "Operand 2", example = "5.25", required = true)
            @RequestParam String operand2) {
        try {
            SubtractCommand command = new SubtractCommand(
                new BigDecimal(operand1),
                new BigDecimal(operand2)
            );
            CalculationResponse response = calculatorInputPort.performSubtraction(command);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/multiply")
    @Operation(summary = "Multiply two numbers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operation successful",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = CalculationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CalculationResponse> multiply(
            @Parameter(description = "Operand 1", example = "10.50", required = true)
            @RequestParam String operand1,
            @Parameter(description = "Operand 2", example = "2.00", required = true)
            @RequestParam String operand2) {
        try {
            MultiplyCommand command = new MultiplyCommand(
                new BigDecimal(operand1),
                new BigDecimal(operand2)
            );
            CalculationResponse response = calculatorInputPort.performMultiplication(command);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/divide")
    @Operation(summary = "Divide two numbers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operation successful",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = CalculationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or division by zero"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CalculationResponse> divide(
            @Parameter(description = "Dividend", example = "10.50", required = true)
            @RequestParam String operand1,
            @Parameter(description = "Divisor (cannot be zero)", example = "2.00", required = true)
            @RequestParam String operand2,
            @Parameter(description = "Scale (decimal places)", example = "2", required = false)
            @RequestParam(defaultValue = "2") int scale) {
        try {
            DivideCommand command = new DivideCommand(
                new BigDecimal(operand1),
                new BigDecimal(operand2),
                scale
            );
            CalculationResponse response = calculatorInputPort.performDivision(command);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        } catch (ArithmeticException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Calculator API is running");
    }
}
