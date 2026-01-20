# Development Guide

## Project Setup

### Prerequisites
- JDK 17+
- Maven 3.6+
- Git
- Docker (optional)

### Environment Setup

```bash
# Clone the repository
git clone https://github.com/yourusername/hexagonal-calculator-api.git
cd hexagonal-calculator-api

# Set Java version
export JAVA_HOME=/path/to/java17
export PATH=$JAVA_HOME/bin:$PATH

# Verify Java version
java -version
```

## Building the Project

### Maven Build

```bash
# Full build with tests
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Run specific tests
mvn test -Dtest=CalculationTest
```

### IDE Setup

#### IntelliJ IDEA
1. File > Open > Select project folder
2. Configure SDK: File > Project Structure > SDK > Java 17
3. Enable annotation processing: Settings > Build > Compiler > Annotation Processors > Enable

#### VS Code
1. Install Extension Pack for Java
2. Install Lombok Annotation Support extension
3. Open project folder

## Running the Application

### Maven
```bash
mvn spring-boot:run
```

### IDE
- Right-click CalculatorApiApplication.java
- Run 'CalculatorApiApplication.main()'

### Docker
```bash
docker-compose up
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test
```bash
mvn test -Dtest=CalculationTest
mvn test -Dtest=CalculatorControllerTest
```

### Test With Coverage
```bash
mvn test jacoco:report
```

## Code Quality

### SonarQube Analysis
```bash
mvn sonar:sonar
```

### CheckStyle
```bash
mvn checkstyle:check
```

## Architecture

The project follows **Hexagonal Architecture**:

- **Domain Layer**: Pure business logic, no framework dependencies
- **Application Layer**: Use cases that orchestrate domain logic
- **Adapter Layer**: Input (REST) and output (Tracer, Persistence) adapters
- **Ports**: Interfaces that define communication contracts

## Adding New Features

### Add New Operation

1. Create Command class:
   ```java
   // src/main/java/io/corp/calculator/domain/ports/input/PowerCommand.java
   @Data
   @AllArgsConstructor
   public class PowerCommand {
       private BigDecimal base;
       private int exponent;
   }
   ```

2. Add to port interface:
   ```java
   CalculationResponse performPower(PowerCommand command);
   ```

3. Add domain logic:
   ```java
   public static Calculation power(BigDecimal base, int exponent) {
       // Implementation
   }
   ```

4. Implement in use case:
   ```java
   @Override
   public CalculationResponse performPower(PowerCommand command) {
       // Implementation
   }
   ```

5. Add REST endpoint:
   ```java
   @PostMapping("/power")
   public ResponseEntity<CalculationResponse> power(...) {
       // Implementation
   }
   ```

6. Add tests in test layer

### Add New Output Adapter

1. Create adapter class:
   ```java
   @Component
   public class DatabaseTracerAdapter implements TracerPort {
       @Override
       public <T> void trace(T result) {
           // Implementation
       }
   }
   ```

2. Switch implementations by disabling/enabling @Component

## Debugging

### Enable Debug Logging
```yaml
logging:
  level:
    io.corp.calculator: DEBUG
```

### Debug with IDE
1. Set breakpoints
2. Run > Debug 'CalculatorApiApplication'
3. Use Debug panel

### Remote Debugging
```bash
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar target/app.jar
```

## Deployment

### Build JAR
```bash
mvn clean package -DskipTests
```

### Build Docker Image
```bash
docker build -t calculator:2.0.0 .
```

### Run Docker Container
```bash
docker run -d -p 8080:8080 calculator:2.0.0
```

## Performance

### Optimize Maven Builds
```bash
mvn -T 1C clean install  # Use 1 core per thread
```

### Monitor Application
```bash
# View logs
docker-compose logs -f

# Check health
curl http://localhost:8080/api/v1/calculator/health
```

## Troubleshooting

### Build Issues

**Issue**: Java version mismatch
```bash
javac -version  # Check compiler version
```

**Issue**: Lombok not working
- Ensure IDE annotation processing is enabled
- Rebuild project

### Runtime Issues

**Issue**: Port 8080 already in use
```bash
lsof -i :8080
kill -9 <PID>
```

**Issue**: Container won't start
```bash
docker logs calculator-api
```

## Git Workflow

```bash
# Create feature branch
git checkout -b feature/new-operation

# Make changes and test
mvn clean install

# Commit
git add .
git commit -m "Add new operation"

# Push
git push origin feature/new-operation

# Create Pull Request
```

## Code Style

- Java 17+ syntax
- Lombok annotations for boilerplate reduction
- SLF4J for logging
- BigDecimal for numeric operations
- Meaningful variable names

## Documentation

Update documentation when:
- Adding new endpoints
- Changing architecture
- Adding new features
- Fixing bugs

## Release Process

1. Update version in pom.xml
2. Update CHANGELOG
3. Create release notes
4. Tag release in Git
5. Build and push Docker image
