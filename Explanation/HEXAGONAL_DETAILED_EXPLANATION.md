# Hexagonal Architecture - Complete Step-by-Step Explanation

## Table of Contents
1. [What is Hexagonal Architecture?](#what-is-hexagonal-architecture)
2. [Why Use Hexagonal Architecture?](#why-use-hexagonal-architecture)
3. [The Theory Behind It](#the-theory-behind-it)
4. [Core Concepts Explained](#core-concepts-explained)
5. [Identifying Hexagonal Architecture](#identifying-hexagonal-architecture)
6. [Our Project Breakdown](#our-project-breakdown)
7. [How to Build It Step-by-Step](#how-to-build-it-step-by-step)

---

## What is Hexagonal Architecture?

### Simple Definition

Hexagonal Architecture (also called **Ports & Adapters** pattern) is a software design pattern that organizes your application so that:

1. **Business logic is independent** from external systems
2. **External systems can be plugged in/unplugged** without changing core logic
3. **Testing is easier** because you can test logic without dependencies
4. **The application can work with multiple interfaces** (HTTP, CLI, Kafka, etc.)

### Why "Hexagonal"?

The name comes from a hexagon shape visualizing the architecture:

```
        ┌─────────────────────────────────┐
        │    External World (HTTP, DB)    │
        └──────────────┬──────────────────┘
                       │
        ┌──────────────┴──────────────┐
        │                             │
        ▼ INPUT ADAPTER          OUTPUT ADAPTER ▼
        │                             │
        │  ┌─────────────────────┐    │
        │  │   INPUT PORTS       │    │
        │  │  (Interfaces)       │    │
        │  └─────────┬───────────┘    │
        │            │                │
        │   ┌────────▼────────┐       │
        │   │    HEXAGON      │       │
        │   │  (Core Business)│       │
        │   │                 │       │
        │   └────────┬────────┘       │
        │            │                │
        │  ┌─────────▼───────────┐    │
        │  │   OUTPUT PORTS      │    │
        │  │  (Interfaces)       │    │
        │  └─────────────────────┘    │
        │                             │
        └─────────────────────────────┘

The hexagon = your business logic
Each side = a port (interface)
Outside = adapters (implementations)
```

The shape doesn't matter - it's just a visual representation of:
- **Center**: Your core business logic
- **Sides**: Ports (input & output)
- **Outside**: Adapters connecting to external world

---

## Why Use Hexagonal Architecture?

### Problem It Solves

**Before Hexagonal Architecture:**

```java
// ❌ TIGHTLY COUPLED CODE
@Controller
public class OrderController {
    
    private OrderService service = new OrderService();  // ❌ Hard-coded dependency
    
    @PostMapping("/order")
    public void createOrder(Order order) {
        service.create(order);
        
        // ❌ Multiple responsibilities
        sendEmail(order);           // Email logic mixed in
        updateDatabase(order);      // Database logic mixed in
        logToFile(order);          // Logging logic mixed in
        callExternalAPI(order);    // External API logic mixed in
    }
}
```

**Problems:**
- ❌ Controller knows about email, database, file system, external APIs
- ❌ Hard to test (need real email, database, etc.)
- ❌ Hard to change email provider (affects controller)
- ❌ Hard to add new interface (Kafka, CLI) without changing business logic
- ❌ Business logic mixed with framework code
- ❌ Can't reuse business logic elsewhere

**After Hexagonal Architecture:**

```java
// ✅ LOOSELY COUPLED CODE
@Controller
public class OrderController {
    
    private CreateOrderUseCase useCase;  // ✅ Depends on interface, not implementation
    
    @PostMapping("/order")
    public void createOrder(Order order) {
        useCase.create(order);  // ✅ Simple, clean, single responsibility
    }
}

// ✅ Business logic is independent
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {
    
    private OrderPort orderPort;          // ✅ Abstract dependency
    private EmailPort emailPort;          // ✅ Abstract dependency
    private NotificationPort notifyPort;  // ✅ Abstract dependency
    
    @Override
    public void create(Order order) {
        order.validate();                 // ✅ Pure business logic
        orderPort.save(order);            // ✅ Delegates to port
        emailPort.sendConfirmation(order); // ✅ Delegates to port
        notifyPort.notify(order);         // ✅ Delegates to port
    }
}
```

**Benefits:**
- ✅ Controller only knows about interfaces
- ✅ Easy to test (use mock implementations)
- ✅ Easy to change email provider (new adapter, no core change)
- ✅ Easy to add new interface (Kafka, CLI, etc.)
- ✅ Business logic is pure and testable
- ✅ Can reuse business logic anywhere

### Key Benefits

| Benefit | Explanation |
|---------|-------------|
| **Testability** | Test business logic without HTTP, DB, or external APIs |
| **Flexibility** | Change implementations without changing core logic |
| **Reusability** | Use same business logic from multiple interfaces |
| **Independence** | Business logic has zero dependencies on frameworks |
| **Scalability** | Easy to add new features and adapters |
| **Maintainability** | Clear structure makes code easy to understand |

---

## The Theory Behind It

### Design Principles Used

Hexagonal Architecture is built on several solid design principles:

#### 1. **Dependency Inversion Principle (DIP)**

**What it says:** High-level modules should not depend on low-level modules. Both should depend on abstractions.

**In Our Project:**

```
❌ WITHOUT DIP (Wrong):
CalculatorService ──────depends on────> ConsoleTracerAdapter
                                         (concrete class)

✅ WITH DIP (Correct):
CalculatorService ──────depends on────> TracerPort (interface)
                                         │
                                         └──implemented by──> ConsoleTracerAdapter
```

**Why?** If ConsoleTracerAdapter changes, CalculatorService breaks. With an interface, you can swap implementations.

#### 2. **Separation of Concerns**

**What it says:** Different concerns should be in different layers.

**In Our Project:**

| Layer | Concern | Example |
|-------|---------|---------|
| **Domain** | Business logic | Calculation.add(), Calculation.divide() |
| **Application** | Orchestration | CalculatorUseCaseImpl coordinates domain |
| **Adapter** | Technology | REST controller, database, external APIs |

#### 3. **The Implicit Interfaces Principle**

**What it says:** Your core domain should define interfaces that external systems must implement.

**In Our Project:**

```java
// ✅ Domain defines what it needs (implicit interface)
public interface TracerPort {
    <T> void trace(T result);  // Domain says: "I need someone to trace things"
}

// ✅ External system implements it
public class ConsoleTracerAdapter implements TracerPort {
    @Override
    public <T> void trace(T result) {
        System.out.println("result :: " + result);
    }
}

// ✅ Domain just calls the interface, doesn't care about implementation
tracerPort.trace(result);  // Works with any TracerPort implementation
```

#### 4. **Inside-Out Dependency Flow**

**What it means:** Dependencies should point INWARD toward the business logic (hexagon core), never outward.

```
WRONG ❌:
Business Logic (Hexagon)
    ↑
    │ depends on
    │
Framework / External

CORRECT ✅:
Framework / External
    ↓
    │ depends on
    │
Business Logic (Hexagon)
```

**In Our Project:**

```
ConsoleTracerAdapter (External)
    ↓
    │ implements
    │
TracerPort (Interface)
    ↓
    │ injected into
    │
CalculatorUseCaseImpl (Business Logic)
    ↓
    │ uses
    │
Calculation (Core Domain)
```

### Architectural Principles

#### 1. **Port = Contract**

A port is an interface that defines a contract:

```java
// ✅ INPUT PORT: Domain tells external systems how to send requests
public interface CalculatorInputPort {
    CalculationResponse performAddition(AddCommand command);
    CalculationResponse performSubtraction(SubtractCommand command);
}

// ✅ OUTPUT PORT: Domain tells external systems what it needs
public interface TracerPort {
    <T> void trace(T result);
}
```

The domain OWNS the ports. Ports are defined FROM the domain's perspective of what it needs.

#### 2. **Adapter = Bridge**

An adapter bridges between the port (interface) and the external system:

```java
// ✅ INPUT ADAPTER: Translates HTTP to domain commands
@RestController
public class CalculatorController {
    
    private CalculatorInputPort useCase;  // Depends on port
    
    @PostMapping("/add")
    public void add(@RequestParam String operand1, @RequestParam String operand2) {
        // Translate HTTP to domain command
        AddCommand command = new AddCommand(
            new BigDecimal(operand1),
            new BigDecimal(operand2)
        );
        
        // Call domain
        useCase.performAddition(command);
    }
}

// ✅ OUTPUT ADAPTER: Translates domain to console output
public class ConsoleTracerAdapter implements TracerPort {
    
    @Override
    public <T> void trace(T result) {
        // Translate domain object to console output
        System.out.println("result :: " + result.toString());
    }
}
```

#### 3. **Hexagon = Pure Business Logic**

The hexagon contains:
- Domain models with business logic
- Use cases that orchestrate domain
- ZERO framework dependencies
- ZERO external system knowledge

```java
// ✅ HEXAGON (No framework, no external dependency)
public class Calculation {
    
    public static Calculation add(BigDecimal operand1, BigDecimal operand2) {
        // ✅ Pure Java, pure logic
        BigDecimal result = operand1.add(operand2);
        return new Calculation(operand1, operand2, "+", result);
    }
}

public class CalculatorUseCaseImpl implements CalculatorInputPort {
    
    private TracerPort tracerPort;          // ✅ Abstract (port)
    private PersistencePort persistencePort; // ✅ Abstract (port)
    
    @Override
    public CalculationResponse performAddition(AddCommand command) {
        // ✅ Pure orchestration of domain logic
        Calculation calc = Calculation.add(
            command.getOperand1(),
            command.getOperand2()
        );
        
        // ✅ Delegate to ports, don't know implementations
        tracerPort.trace(calc);
        persistencePort.save(calc);
        
        return toResponse(calc);
    }
}
```

---

## Core Concepts Explained

### 1. Domain Model

**What it is:** Objects that represent your business concepts and contain business logic.

**Characteristics:**
- No annotations (@Component, @Entity, @Controller)
- No framework dependencies
- Pure Java with business methods
- Immutable or semi-immutable
- Contains validation logic

**In Our Project:**

```java
public class Calculation {
    
    private BigDecimal operand1;
    private BigDecimal operand2;
    private String operation;
    private BigDecimal result;
    
    // ✅ Pure business logic methods
    public static Calculation add(BigDecimal operand1, BigDecimal operand2) {
        if (operand1 == null || operand2 == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }
        BigDecimal result = operand1.add(operand2);
        return new Calculation(operand1, operand2, "+", result);
    }
    
    public static Calculation divide(BigDecimal operand1, BigDecimal operand2, int scale) {
        if (operand2.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        BigDecimal result = operand1.divide(operand2, scale, RoundingMode.HALF_UP);
        return new Calculation(operand1, operand2, "/", result);
    }
}
```

**Why separate from Spring?**
- If you change from Spring to Quarkus, Calculation still works
- If you want to use this logic in a batch job, it works
- If you want to use it in a CLI app, it works
- Testing doesn't need Spring context

### 2. Ports (Interfaces)

**What they are:** Interfaces that define contracts between hexagon and external world.

**Two Types:**

#### Input Ports

Define operations the hexagon can perform (what use cases it provides).

```java
// ✅ INPUT PORT: Domain tells external systems what it can do
public interface CalculatorInputPort {
    
    CalculationResponse performAddition(AddCommand command);
    CalculationResponse performSubtraction(SubtractCommand command);
    CalculationResponse performMultiplication(MultiplyCommand command);
    CalculationResponse performDivision(DivideCommand command);
}
```

**Characteristics:**
- Defined by domain (domain says what it can do)
- Implemented by use cases
- Called by input adapters
- Uses command/response objects (DTOs)

#### Output Ports

Define services the hexagon needs (what external systems it depends on).

```java
// ✅ OUTPUT PORT: Domain tells external systems what it needs
public interface TracerPort {
    <T> void trace(T result);
}

public interface PersistencePort {
    void save(Calculation calculation);
    Optional<Calculation> findById(String id);
}
```

**Characteristics:**
- Defined by domain (domain says what it needs)
- Implemented by output adapters
- Injected into use cases
- Domain calls them, doesn't care about implementations

### 3. Adapters

**What they are:** Implementations that connect ports to external systems.

**Two Types:**

#### Input Adapters

Translate from external interface to domain.

```java
// ✅ INPUT ADAPTER: REST ─→ Domain
@RestController
@RequestMapping("/api/v1/calculator")
public class CalculatorController {
    
    private CalculatorInputPort calculatorInputPort;  // Depends on port
    
    @PostMapping("/add")
    public ResponseEntity<CalculationResponse> add(
            @RequestParam String operand1,
            @RequestParam String operand2) {
        
        // Step 1: Translate from HTTP/REST to domain command
        AddCommand command = new AddCommand(
            new BigDecimal(operand1),
            new BigDecimal(operand2)
        );
        
        // Step 2: Call domain
        CalculationResponse response = calculatorInputPort.performAddition(command);
        
        // Step 3: Translate from domain response to HTTP/JSON
        return ResponseEntity.ok(response);
    }
}

// Flow:
// HTTP Request
//     ↓
// REST Adapter receives it
//     ↓
// Adapter translates to domain command
//     ↓
// Adapter calls input port
//     ↓
// Use case executes domain logic
//     ↓
// Use case returns response
//     ↓
// Adapter translates to HTTP response
//     ↓
// HTTP Response back to client
```

**Why separate?**
- Can add Kafka adapter without changing domain
- Can add CLI adapter without changing domain
- Can add GraphQL adapter without changing domain

#### Output Adapters

Translate from domain to external system.

```java
// ✅ OUTPUT ADAPTER: Domain ─→ Console
public class ConsoleTracerAdapter implements TracerPort {
    
    @Override
    public <T> void trace(T result) {
        // Translate domain object to console output
        System.out.println("result :: " + result.toString());
    }
}

// Flow:
// Use case calls tracerPort.trace(calculation)
//     ↓
// Adapter receives domain object (Calculation)
//     ↓
// Adapter translates to console output
//     ↓
// System.out.println()

// ✅ OUTPUT ADAPTER: Domain ─→ Database (Alternative)
public class DatabasePersistenceAdapter implements PersistencePort {
    
    private CalculationRepository repository;
    
    @Override
    public void save(Calculation calculation) {
        // Translate domain to database entity
        CalculationEntity entity = new CalculationEntity(
            calculation.getOperand1(),
            calculation.getOperand2(),
            calculation.getOperation(),
            calculation.getResult()
        );
        
        repository.save(entity);
    }
}

// Can swap ConsoleTracerAdapter with DatabaseTracerAdapter
// without changing ANY business logic!
```

**Why separate?**
- Easy to change tracer (console → database)
- Easy to add new ports (logging, cache)
- Business logic doesn't know about implementation details

### 4. Use Cases

**What they are:** Orchestrators that coordinate domain logic and delegate to ports.

**Characteristics:**
- Implements input port
- Uses output ports
- Coordinates domain entities
- Handles application flow
- Thin layer (no heavy logic)

```java
public class CalculatorUseCaseImpl implements CalculatorInputPort {
    
    private final TracerPort tracerPort;
    private final PersistencePort persistencePort;
    
    @Override
    public CalculationResponse performAddition(AddCommand command) {
        
        // Step 1: Validate (business rule)
        if (command.getOperand1() == null || command.getOperand2() == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }
        
        // Step 2: Use domain logic
        Calculation calculation = Calculation.add(
            command.getOperand1(),
            command.getOperand2()
        );
        
        // Step 3: Delegate to output ports
        tracerPort.trace(calculation);      // Tell someone to trace it
        persistencePort.save(calculation);  // Tell someone to save it
        
        // Step 4: Return response
        return toResponse(calculation);
    }
}

// It's like a conductor orchestrating musicians:
// - Conductor doesn't play instruments (domain does)
// - Conductor doesn't know which orchestra (adapters do)
// - Conductor just coordinates the flow
```

---

## Identifying Hexagonal Architecture

### Signs You're Looking at Hexagonal Architecture

#### 1. **Ports (Interfaces) are defined by domain**

```java
// ✅ SIGN OF HEXAGONAL ARCHITECTURE
// These interfaces are in domain layer
public interface TracerPort {  // Domain says "I need a tracer"
    <T> void trace(T result);
}

public interface PersistencePort {  // Domain says "I need persistence"
    void save(Calculation calculation);
}

// NOT
// @Repository  ← This is framework specific
// public interface CalculationRepository  ← This is defined by repository layer
```

#### 2. **No framework annotations in domain**

```java
// ✅ SIGN OF HEXAGONAL ARCHITECTURE (Pure domain)
public class Calculation {
    // ✅ No @Entity, @Component, @Service
    // ✅ No annotations at all
    // ✅ Pure Java
    
    public static Calculation add(BigDecimal a, BigDecimal b) {
        return new Calculation(a, b, "+", a.add(b));
    }
}

// NOT
// @Entity  ← Framework annotation
// public class Calculation {
```

#### 3. **Dependencies point inward**

```
// ✅ SIGN OF HEXAGONAL ARCHITECTURE (Correct flow)

REST Endpoint
    │
    └─ depends on → Input Port (Interface)
                        │
                        └─ implemented by → Use Case
                                             │
                                             └─ uses → Domain
                                                       │
                                                       └─ needs → Output Port
                                                                   │
                                                                   └─ implemented by → Adapter

Everyone points toward the center (hexagon)!

// NOT
Domain depends on Spring
Domain depends on Database
Domain depends on REST
```

#### 4. **Easy to test without frameworks/dependencies**

```java
// ✅ SIGN OF HEXAGONAL ARCHITECTURE
@Test
public void testAddition() {
    // Can test without HTTP, DB, or frameworks
    Calculation result = Calculation.add(
        new BigDecimal("10"),
        new BigDecimal("5")
    );
    
    assertEquals(new BigDecimal("15"), result.getResult());
}

// ✅ Can test use case with mocks
@Test
public void testAdditionUseCase() {
    TracerPort mockTracer = mock(TracerPort.class);
    PersistencePort mockPersistence = mock(PersistencePort.class);
    
    CalculatorInputPort useCase = new CalculatorUseCaseImpl(
        mockTracer,
        mockPersistence
    );
    
    CalculationResponse response = useCase.performAddition(
        new AddCommand(new BigDecimal("10"), new BigDecimal("5"))
    );
    
    assertEquals(new BigDecimal("15"), response.getResult());
    verify(mockTracer).trace(any());
    verify(mockPersistence).save(any());
}
```

#### 5. **Adapters can be swapped without changing core**

```java
// ✅ SIGN OF HEXAGONAL ARCHITECTURE

// Original: Console tracer
@Bean
public TracerPort tracerPort() {
    return new ConsoleTracerAdapter();
}

// Change to: Database tracer
@Bean
public TracerPort tracerPort() {
    return new DatabaseTracerAdapter(repository);
}

// No changes needed to CalculatorUseCaseImpl!
// No changes needed to any business logic!
// This is the power of hexagonal architecture
```

#### 6. **Commands and Responses are simple DTOs**

```java
// ✅ SIGN OF HEXAGONAL ARCHITECTURE
@Data
@AllArgsConstructor
public class AddCommand {
    private BigDecimal operand1;
    private BigDecimal operand2;
    // ✅ No framework annotations
    // ✅ No logic
    // ✅ Just data
}

@Data
@AllArgsConstructor
public class CalculationResponse {
    private BigDecimal operand1;
    private BigDecimal operand2;
    private String operation;
    private BigDecimal result;
    // ✅ No framework annotations
    // ✅ No logic
    // ✅ Just data
}
```

### Checklist: Is This Hexagonal Architecture?

Use this checklist to identify hexagonal architecture:

- [ ] Domain layer has NO framework annotations
- [ ] Domain layer has NO external dependencies
- [ ] Ports are interfaces defined by domain
- [ ] Adapters implement ports
- [ ] Dependencies point inward (toward domain)
- [ ] Can test domain logic without mocks
- [ ] Can test use cases with mock ports
- [ ] Adapters can be swapped easily
- [ ] Commands/responses are simple DTOs
- [ ] No circular dependencies
- [ ] Clear separation of layers
- [ ] Each adapter has a single responsibility

---

## Our Project Breakdown

Let me show you exactly where each component lives in our actual project:

### Project Structure

```
hexagonal-calculator-api/
│
├── src/main/java/io/corp/calculator/
│   │
│   ├── domain/                          ← 🎯 THE HEXAGON
│   │   ├── model/
│   │   │   └── Calculation.java         ← Domain Entity (Business Logic)
│   │   ├── ports/
│   │   │   ├── input/
│   │   │   │   ├── CalculatorInputPort.java    ← Input Port (Interface)
│   │   │   │   ├── AddCommand.java             ← Command DTO
│   │   │   │   ├── SubtractCommand.java        ← Command DTO
│   │   │   │   ├── MultiplyCommand.java        ← Command DTO
│   │   │   │   ├── DivideCommand.java          ← Command DTO
│   │   │   │   └── CalculationResponse.java    ← Response DTO
│   │   │   └── output/
│   │   │       ├── TracerPort.java             ← Output Port (Interface)
│   │   │       └── PersistencePort.java        ← Output Port (Interface)
│   │   └── exception/
│   │       └── DomainException.java
│   │
│   ├── application/                     ← 🎯 USE CASES (Orchestration)
│   │   ├── service/
│   │   │   └── CalculatorUseCaseImpl.java ← Use Case Implementation
│   │   └── config/
│   │       └── ApplicationConfig.java    ← Wiring
│   │
│   ├── adapter/                         ← 🎯 ADAPTERS (External Systems)
│   │   ├── in/web/
│   │   │   └── CalculatorController.java ← Input Adapter (REST)
│   │   ├── out/
│   │   │   ├── tracer/
│   │   │   │   └── ConsoleTracerAdapter.java   ← Output Adapter
│   │   │   └── persistence/
│   │   │       └── InMemoryPersistenceAdapter.java ← Output Adapter
│   │   └── config/
│   │       └── AdapterConfig.java
│   │
│   └── CalculatorApiApplication.java   ← Spring Boot Entry Point
│
└── src/test/java/io/corp/calculator/
    ├── domain/model/
    │   └── CalculationTest.java          ← Domain logic tests
    ├── application/service/
    │   └── CalculatorUseCaseImplTest.java ← Use case tests
    └── adapter/in/web/
        └── CalculatorControllerTest.java  ← Integration tests
```

### Layer-by-Layer Breakdown

#### LAYER 1: DOMAIN (The Hexagon Core)

**What's here:** Pure business logic with NO framework code

**Files:**
```
domain/model/Calculation.java
domain/ports/input/CalculatorInputPort.java (interface)
domain/ports/input/AddCommand.java (DTO)
domain/ports/input/CalculationResponse.java (DTO)
domain/ports/output/TracerPort.java (interface)
domain/ports/output/PersistencePort.java (interface)
```

**Example - Pure Domain Logic:**

```java
// ✅ NO framework imports
// ✅ NO Spring, NO JPA, NO Jackson
// ✅ Pure Java business logic

public class Calculation {
    private BigDecimal operand1;
    private BigDecimal operand2;
    private String operation;
    private BigDecimal result;
    
    // ✅ Pure business method - no framework involved
    public static Calculation add(BigDecimal operand1, BigDecimal operand2) {
        // ✅ Validation (business rule)
        if (operand1 == null || operand2 == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }
        
        // ✅ Calculation (business logic)
        BigDecimal result = operand1.add(operand2);
        
        // ✅ Create and return (no persistence, no HTTP, no framework)
        return new Calculation(operand1, operand2, "+", result);
    }
}

// ✅ Input Port Interface (Domain says what it provides)
public interface CalculatorInputPort {
    CalculationResponse performAddition(AddCommand command);
}

// ✅ Output Port Interface (Domain says what it needs)
public interface TracerPort {
    <T> void trace(T result);
}
```

**Why is this Hexagonal?**
- ✅ Contains business logic
- ✅ Defines input ports (what it can do)
- ✅ Defines output ports (what it needs)
- ✅ Zero framework dependencies
- ✅ Zero external system knowledge

#### LAYER 2: APPLICATION (Use Cases)

**What's here:** Orchestration of domain logic using ports

**Files:**
```
application/service/CalculatorUseCaseImpl.java
application/config/ApplicationConfig.java
```

**Example - Use Case:**

```java
public class CalculatorUseCaseImpl implements CalculatorInputPort {
    
    // ✅ Injected dependencies are abstract (ports/interfaces)
    private final TracerPort tracerPort;
    private final PersistencePort persistencePort;
    
    public CalculatorUseCaseImpl(TracerPort tracerPort, PersistencePort persistencePort) {
        this.tracerPort = tracerPort;
        this.persistencePort = persistencePort;
    }
    
    // ✅ Implements input port method
    @Override
    public CalculationResponse performAddition(AddCommand command) {
        
        // Step 1: Use domain logic
        Calculation calculation = Calculation.add(
            command.getOperand1(),
            command.getOperand2()
        );
        
        // Step 2: Delegate to output ports
        tracerPort.trace(calculation);      // ✅ Don't know how tracing happens
        persistencePort.save(calculation);  // ✅ Don't know how saving happens
        
        // Step 3: Return response
        return new CalculationResponse(
            calculation.getOperand1(),
            calculation.getOperand2(),
            calculation.getOperation(),
            calculation.getResult()
        );
    }
}

// ✅ Configuration wires everything together
@Configuration
public class ApplicationConfig {
    
    @Bean
    public CalculatorInputPort calculatorInputPort(
            TracerPort tracerPort,
            PersistencePort persistencePort) {
        
        return new CalculatorUseCaseImpl(tracerPort, persistencePort);
    }
}
```

**Why is this Hexagonal?**
- ✅ Implements input port
- ✅ Uses output ports (abstract)
- ✅ Coordinates domain logic
- ✅ Doesn't know about implementations

#### LAYER 3: ADAPTERS - INPUT (External Interface to Domain)

**What's here:** REST endpoint that translates HTTP to domain commands

**Files:**
```
adapter/in/web/CalculatorController.java
```

**Example - Input Adapter:**

```java
@RestController
@RequestMapping("/api/v1/calculator")
public class CalculatorController {
    
    // ✅ Depends on input port (interface, not implementation)
    private final CalculatorInputPort calculatorInputPort;
    
    public CalculatorController(CalculatorInputPort calculatorInputPort) {
        this.calculatorInputPort = calculatorInputPort;
    }
    
    // ✅ REST endpoint (external interface)
    @PostMapping("/add")
    public ResponseEntity<CalculationResponse> add(
            @RequestParam String operand1,
            @RequestParam String operand2) {
        
        try {
            // Step 1: Translate HTTP request to domain command
            AddCommand command = new AddCommand(
                new BigDecimal(operand1),      // Parse string to BigDecimal
                new BigDecimal(operand2)
            );
            
            // Step 2: Call domain through port
            CalculationResponse response = calculatorInputPort.performAddition(command);
            
            // Step 3: Translate domain response to HTTP response
            return ResponseEntity.ok(response);  // Convert to JSON
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

// Flow:
// HTTP Request (String parameters)
//     ↓
// Adapter translates to domain command
//     ↓
// Adapter calls input port
//     ↓
// Use case executes
//     ↓
// Use case returns response
//     ↓
// Adapter translates to HTTP response (JSON)
//     ↓
// HTTP Response
```

**Why is this Hexagonal?**
- ✅ Translates HTTP to domain
- ✅ Depends on input port (interface)
- ✅ Can add another adapter (Kafka, CLI) without changing domain

#### LAYER 3: ADAPTERS - OUTPUT (Domain to External Systems)

**What's here:** Implementations of output ports

**Files:**
```
adapter/out/tracer/ConsoleTracerAdapter.java
adapter/out/persistence/InMemoryPersistenceAdapter.java
```

**Example - Output Adapters:**

```java
// ✅ OUTPUT ADAPTER 1: Tracer
public class ConsoleTracerAdapter implements TracerPort {
    
    @Override
    public <T> void trace(T result) {
        // ✅ Implements port by printing to console
        System.out.println("result :: " + result.toString());
    }
}

// ✅ OUTPUT ADAPTER 2: Persistence
public class InMemoryPersistenceAdapter implements PersistencePort {
    
    private final Map<String, Calculation> store = new HashMap<>();
    
    @Override
    public void save(Calculation calculation) {
        // ✅ Implements port by saving to memory
        String id = UUID.randomUUID().toString();
        store.put(id, calculation);
    }
    
    @Override
    public Optional<Calculation> findById(String id) {
        // ✅ Implements port by finding from memory
        return Optional.ofNullable(store.get(id));
    }
}

// ✅ Configuration provides adapters
@Configuration
public class AdapterConfig {
    
    @Bean
    public TracerPort tracerPort() {
        return new ConsoleTracerAdapter();  // ← Could be DatabaseTracerAdapter
    }
    
    @Bean
    public PersistencePort persistencePort() {
        return new InMemoryPersistenceAdapter();  // ← Could be DatabasePersistenceAdapter
    }
}
```

**Why is this Hexagonal?**
- ✅ Implements output ports
- ✅ Can be swapped for alternatives
- ✅ Domain doesn't know about them

### The Complete Flow

Let me show you the complete request flow through the hexagonal layers:

```
1. USER MAKES REQUEST
   HTTP: POST /api/v1/calculator/add?operand1=10.50&operand2=5.25
   
2. INPUT ADAPTER RECEIVES
   CalculatorController.add()
   │
   ├─ Parse URL parameters
   │  operand1 = "10.50" → BigDecimal(10.50)
   │  operand2 = "5.25" → BigDecimal(5.25)
   │
   └─ Create domain command
      AddCommand cmd = new AddCommand(BigDecimal(10.50), BigDecimal(5.25))

3. INPUT PORT CALLED
   calculatorInputPort.performAddition(cmd)
   │
   └─ Calls use case implementation

4. USE CASE EXECUTES
   CalculatorUseCaseImpl.performAddition()
   │
   ├─ Call domain logic
   │  Calculation calc = Calculation.add(10.50, 5.25)
   │  │
   │  └─ Domain calculates: 10.50 + 5.25 = 15.75
   │
   ├─ Delegate to output port 1 (Tracer)
   │  tracerPort.trace(calc)
   │  │
   │  └─ ConsoleTracerAdapter.trace(calc)
   │      └─ System.out: "result :: Calculation(...result=15.75)"
   │
   ├─ Delegate to output port 2 (Persistence)
   │  persistencePort.save(calc)
   │  │
   │  └─ InMemoryPersistenceAdapter.save(calc)
   │      └─ Store in HashMap
   │
   └─ Return response
      CalculationResponse(10.50, 5.25, "+", 15.75)

5. INPUT ADAPTER RESPONDS
   CalculatorController
   │
   ├─ Receive response from use case
   ├─ Translate to HTTP/JSON
   └─ Return ResponseEntity.ok(response)

6. USER RECEIVES RESPONSE
   HTTP 200 OK
   {
     "operand1": 10.50,
     "operand2": 5.25,
     "operation": "+",
     "result": 15.75
   }
```

---

## How to Build It Step-by-Step

### Phase 1: Design (Before coding)

#### Step 1.1: Identify Your Core Domain

**Ask:** What is the business logic that's independent of any framework?

**For Calculator:**
```
Core domain = Arithmetic operations
- Add two numbers
- Subtract two numbers
- Multiply two numbers
- Divide two numbers

Business rules:
- No null operands
- No division by zero
- Use BigDecimal for precision
```

#### Step 1.2: Design Your Domain Model

Create a domain model that contains pure business logic:

```java
// Step 1: Identify what the domain needs to do
public class Calculation {
    
    // What it has (state)
    private BigDecimal operand1;
    private BigDecimal operand2;
    private String operation;
    private BigDecimal result;
    
    // What it can do (behavior)
    public static Calculation add(BigDecimal a, BigDecimal b) { ... }
    public static Calculation subtract(BigDecimal a, BigDecimal b) { ... }
    public static Calculation multiply(BigDecimal a, BigDecimal b) { ... }
    public static Calculation divide(BigDecimal a, BigDecimal b, int scale) { ... }
}
```

#### Step 1.3: Identify Input Ports

**Ask:** What external systems need to send requests to my domain?

**For Calculator:**
- REST API (HTTP requests)
- Maybe: CLI, Kafka, GraphQL (in future)

**Define the port:**

```java
// Step 2: What operations does the domain provide?
public interface CalculatorInputPort {
    
    // These are the operations external systems can request
    CalculationResponse performAddition(AddCommand command);
    CalculationResponse performSubtraction(SubtractCommand command);
    CalculationResponse performMultiplication(MultiplyCommand command);
    CalculationResponse performDivision(DivideCommand command);
}

// What data does the domain need to perform these operations?
@Data
@AllArgsConstructor
public class AddCommand {
    private BigDecimal operand1;
    private BigDecimal operand2;
}

// What data does the domain return?
@Data
@AllArgsConstructor
public class CalculationResponse {
    private BigDecimal operand1;
    private BigDecimal operand2;
    private String operation;
    private BigDecimal result;
}
```

#### Step 1.4: Identify Output Ports

**Ask:** What external systems does my domain need to interact with?

**For Calculator:**
- Tracing/Logging (need to notify someone about results)
- Persistence (need to save calculations)

**Define the ports:**

```java
// Step 3: What external services does the domain need?

// Port 1: Domain needs to tell someone about results
public interface TracerPort {
    <T> void trace(T result);
}

// Port 2: Domain needs to persist calculations
public interface PersistencePort {
    void save(Calculation calculation);
    Optional<Calculation> findById(String id);
}
```

### Phase 2: Implementation (Coding the architecture)

#### Step 2.1: Implement the Domain

Create the domain model with pure business logic:

```java
// ✅ Pure domain (no framework)
public class Calculation {
    
    private BigDecimal operand1;
    private BigDecimal operand2;
    private String operation;
    private BigDecimal result;
    
    public static Calculation add(BigDecimal operand1, BigDecimal operand2) {
        if (operand1 == null || operand2 == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }
        BigDecimal result = operand1.add(operand2);
        return new Calculation(operand1, operand2, "+", result);
    }
    
    // ... more operations ...
}
```

**Key Rules:**
- NO @Entity, @Component, @Service
- NO Spring imports
- NO database imports
- NO HTTP imports
- Just pure Java business logic

#### Step 2.2: Implement Input Port

Create use case that implements input port:

```java
// ✅ Orchestration layer (application)
public class CalculatorUseCaseImpl implements CalculatorInputPort {
    
    // Dependencies are abstract (ports)
    private final TracerPort tracerPort;
    private final PersistencePort persistencePort;
    
    public CalculatorUseCaseImpl(
            TracerPort tracerPort,
            PersistencePort persistencePort) {
        this.tracerPort = tracerPort;
        this.persistencePort = persistencePort;
    }
    
    @Override
    public CalculationResponse performAddition(AddCommand command) {
        
        // Use domain logic
        Calculation calculation = Calculation.add(
            command.getOperand1(),
            command.getOperand2()
        );
        
        // Delegate to output ports
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
```

**Key Points:**
- Implements input port
- Uses output ports (injected)
- Orchestrates domain logic
- Doesn't know about implementations

#### Step 2.3: Implement Input Adapter

Create REST controller that translates HTTP to domain:

```java
// ✅ Input adapter (web)
@RestController
@RequestMapping("/api/v1/calculator")
public class CalculatorController {
    
    // Depends on input port (interface)
    private final CalculatorInputPort calculatorInputPort;
    
    public CalculatorController(CalculatorInputPort calculatorInputPort) {
        this.calculatorInputPort = calculatorInputPort;
    }
    
    @PostMapping("/add")
    public ResponseEntity<CalculationResponse> add(
            @RequestParam String operand1,
            @RequestParam String operand2) {
        
        try {
            // Translate HTTP to domain command
            AddCommand command = new AddCommand(
                new BigDecimal(operand1),
                new BigDecimal(operand2)
            );
            
            // Call domain through port
            CalculationResponse response = calculatorInputPort.performAddition(command);
            
            // Translate domain response to HTTP
            return ResponseEntity.ok(response);
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
```

**Key Points:**
- Uses @RestController, @PostMapping (framework)
- Depends on input port (abstract)
- Translates between HTTP and domain
- Can add another adapter (Kafka) without changing domain

#### Step 2.4: Implement Output Adapters

Create implementations of output ports:

```java
// ✅ Output adapter: Tracer
public class ConsoleTracerAdapter implements TracerPort {
    
    @Override
    public <T> void trace(T result) {
        System.out.println("result :: " + result.toString());
    }
}

// ✅ Output adapter: Persistence
public class InMemoryPersistenceAdapter implements PersistencePort {
    
    private final Map<String, Calculation> store = new HashMap<>();
    
    @Override
    public void save(Calculation calculation) {
        String id = UUID.randomUUID().toString();
        store.put(id, calculation);
    }
    
    @Override
    public Optional<Calculation> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
```

**Key Points:**
- Implement output ports
- Can be swapped for alternatives
- Domain doesn't depend on them

#### Step 2.5: Create Configuration

Wire everything together with Spring configuration:

```java
// ✅ Wiring everything together
@Configuration
public class ApplicationConfig {
    
    @Bean
    public CalculatorInputPort calculatorInputPort(
            TracerPort tracerPort,
            PersistencePort persistencePort) {
        
        return new CalculatorUseCaseImpl(tracerPort, persistencePort);
    }
}

@Configuration
public class AdapterConfig {
    
    @Bean
    public TracerPort tracerPort() {
        return new ConsoleTracerAdapter();
    }
    
    @Bean
    public PersistencePort persistencePort() {
        return new InMemoryPersistenceAdapter();
    }
}
```

**Key Point:** All wiring happens in configuration, not in domain

### Phase 3: Testing (Proving it's hexagonal)

#### Step 3.1: Test Domain Logic (No mocks needed)

```java
@Test
public void testAddition() {
    Calculation result = Calculation.add(
        new BigDecimal("10"),
        new BigDecimal("5")
    );
    
    assertEquals(new BigDecimal("15"), result.getResult());
}
```

**Why this proves hexagonal?**
- Can test without any mocks
- Can test without HTTP, database, or frameworks
- Pure business logic is testable

#### Step 3.2: Test Use Cases (With mock ports)

```java
@Test
public void testAdditionUseCase() {
    // Create mock implementations of output ports
    TracerPort mockTracer = mock(TracerPort.class);
    PersistencePort mockPersistence = mock(PersistencePort.class);
    
    // Create use case with mocks (NOT real implementations)
    CalculatorInputPort useCase = new CalculatorUseCaseImpl(
        mockTracer,
        mockPersistence
    );
    
    // Test the use case
    CalculationResponse response = useCase.performAddition(
        new AddCommand(
            new BigDecimal("10"),
            new BigDecimal("5")
        )
    );
    
    // Verify behavior
    assertEquals(new BigDecimal("15"), response.getResult());
    
    // Verify ports were called
    verify(mockTracer).trace(any());
    verify(mockPersistence).save(any());
}
```

**Why this proves hexagonal?**
- Can test use case without real adapters
- Ports are abstract (can be mocked)
- Loose coupling allows easy testing

#### Step 3.3: Test Adapters (Integration)

```java
@SpringBootTest
@AutoConfigureMockMvc
public class CalculatorControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testAddEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/calculator/add")
            .param("operand1", "10")
            .param("operand2", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("15"));
    }
}
```

### Phase 4: Extensibility (Proof it works)

To prove hexagonal architecture works, add a new adapter WITHOUT changing domain:

#### Step 4.1: Add Kafka Input Adapter

```java
// ✅ New input adapter (Kafka) - NO changes to domain!
@Component
public class CalculatorKafkaAdapter {
    
    private final CalculatorInputPort calculatorInputPort;
    
    @KafkaListener(topics = "calculator-add")
    public void handleAdd(AddEvent event) {
        // Translate Kafka event to domain command
        AddCommand command = new AddCommand(
            event.getOperand1(),
            event.getOperand2()
        );
        
        // Call domain
        calculatorInputPort.performAddition(command);
    }
}

// Result:
// - Kafka adapter added
// - Domain logic unchanged ✅
// - REST adapter unchanged ✅
// - Use case unchanged ✅
// - Output adapters unchanged ✅
```

#### Step 4.2: Add Database Output Adapter

```java
// ✅ New output adapter (Database) - NO changes to domain!
@Component
public class DatabasePersistenceAdapter implements PersistencePort {
    
    private final CalculationRepository repository;
    
    @Override
    public void save(Calculation calculation) {
        CalculationEntity entity = new CalculationEntity(
            calculation.getOperand1(),
            calculation.getOperand2(),
            calculation.getOperation(),
            calculation.getResult()
        );
        repository.save(entity);
    }
    
    @Override
    public Optional<Calculation> findById(String id) {
        return repository.findById(id).map(e -> 
            new Calculation(
                e.getOperand1(),
                e.getOperand2(),
                e.getOperation(),
                e.getResult()
            )
        );
    }
}

// To use it, just change configuration:
@Bean
public PersistencePort persistencePort(CalculationRepository repository) {
    return new DatabasePersistenceAdapter(repository);  // Changed!
}

// Result:
// - Database adapter added
// - Domain logic unchanged ✅
// - REST adapter unchanged ✅
// - Use case unchanged ✅
// - Other adapters unchanged ✅
```

This is the power of hexagonal architecture!

---

## Summary

### Why Our Project is Hexagonal Architecture

1. **Core Domain is Independent**
   - Calculation.java has zero framework dependencies
   - Pure business logic
   - Can be used anywhere

2. **Clear Input Ports**
   - CalculatorInputPort defines what domain provides
   - Commands and responses are DTOs
   - REST adapter implements translation

3. **Clear Output Ports**
   - TracerPort and PersistencePort are abstract
   - Domain doesn't know implementations
   - Adapters can be swapped

4. **Layered Structure**
   - Domain (business logic)
   - Application (use cases)
   - Adapters (HTTP, database, etc.)

5. **Dependencies Point Inward**
   - Adapters depend on ports
   - Ports are defined by domain
   - Everything points toward hexagon center

6. **Easy to Test**
   - Domain tested without mocks
   - Use cases tested with mock ports
   - Adapters integration tested

7. **Easy to Extend**
   - Add new adapters without changing domain
   - Add new ports without changing logic
   - Multiple interfaces supported

### How to Build It

1. **Identify domain logic**
2. **Define input ports (what domain provides)**
3. **Define output ports (what domain needs)**
4. **Implement domain with business logic**
5. **Implement use cases that use ports**
6. **Implement input adapters (external → domain)**
7. **Implement output adapters (domain → external)**
8. **Wire with configuration**
9. **Test each layer independently**
10. **Extend with new adapters**

This is hexagonal architecture!
