# Hexagonal Calculator API - Version 2.0

A production-ready REST API for arithmetic operations with **BigDecimal precision**, built using **Hexagonal Architecture (Ports & Adapters)**, complete with **OpenAPI/Swagger documentation**, **Docker support**, and **comprehensive unit tests**.

## 🎯 Features

✅ **Hexagonal Architecture** - Clean separation of concerns (domain, application, adapters)
✅ **BigDecimal Precision** - Accurate decimal arithmetic without floating-point errors
✅ **OpenAPI/Swagger UI** - Interactive API documentation at `/swagger-ui.html`
✅ **Unit Tests** - Comprehensive test suite with >80% code coverage
✅ **Docker Support** - Run with Docker or Docker Compose
✅ **Maven Build** - Easy build and deployment process
✅ **Lombok** - Reduced boilerplate code
✅ **Logging** - SLF4J logging with configurable levels

## 📐 Architecture Overview

### Hexagonal Architecture Layers

```
External Systems (HTTP, Database, etc.)
        │
        ▼
    INPUT ADAPTERS
        │
        ├─ REST Controller
        │
        ▼
    INPUT PORTS (Interfaces)
        │
        ├─ CalculatorInputPort
        │
        ▼
    HEXAGON (Core Domain)
        │
        ├─ Calculation (Domain Model)
        ├─ CalculatorUseCaseImpl (Use Cases)
        │
        ▼
    OUTPUT PORTS (Interfaces)
        │
        ├─ TracerPort
        ├─ PersistencePort
        │
        ▼
    OUTPUT ADAPTERS
        │
        ├─ ConsoleTracerAdapter
        ├─ InMemoryPersistenceAdapter
        │
        ▼
External Systems
```

### Project Structure

```
hexagonal-calculator-api/
├── src/main/java/io/corp/calculator/
│   ├── domain/                          # CORE HEXAGON
│   │   ├── model/
│   │   │   └── Calculation.java         # Domain entity with business logic
│   │   ├── ports/
│   │   │   ├── input/
│   │   │   │   ├── CalculatorInputPort.java
│   │   │   │   ├── AddCommand.java
│   │   │   │   ├── SubtractCommand.java
│   │   │   │   ├── MultiplyCommand.java
│   │   │   │   ├── DivideCommand.java
│   │   │   │   └── CalculationResponse.java
│   │   │   └── output/
│   │   │       ├── TracerPort.java
│   │   │       └── PersistencePort.java
│   │   └── exception/
│   │       └── DomainException.java
│   │
│   ├── application/                    # USE CASES
│   │   ├── service/
│   │   │   └── CalculatorUseCaseImpl.java
│   │   └── config/
│   │       └── ApplicationConfig.java
│   │
│   ├── adapter/                        # ADAPTERS
│   │   ├── in/web/
│   │   │   └── CalculatorController.java
│   │   ├── out/
│   │   │   ├── tracer/
│   │   │   │   └── ConsoleTracerAdapter.java
│   │   │   └── persistence/
│   │   │       └── InMemoryPersistenceAdapter.java
│   │   └── config/
│   │       └── AdapterConfig.java
│   │
│   └── CalculatorApiApplication.java
│
├── src/test/java/io/corp/calculator/
│   ├── domain/model/
│   │   └── CalculationTest.java
│   ├── application/service/
│   │   └── CalculatorUseCaseImplTest.java
│   └── adapter/in/web/
│       └── CalculatorControllerTest.java
│
├── src/main/resources/
│   └── application.yml
│
├── pom.xml                              # Maven configuration
├── Dockerfile                           # Docker image definition
├── docker-compose.yml                   # Docker Compose configuration
├── .dockerignore                        # Docker ignore patterns
├── .gitignore                           # Git ignore patterns
└── README.md                            # This file
```

## 🚀 Quick Start

### Prerequisites

- **Java 17+** (for Maven builds)
- **Maven 3.6+** (for Maven builds)
- **Docker & Docker Compose** (for Docker deployment)
- **Git** (for cloning the repository)

### Option 1: Run with Maven

#### 1. Clone the repository

```bash
git clone https://github.com/yourusername/hexagonal-calculator-api.git
cd hexagonal-calculator-api
```

#### 2. Build the project

```bash
# Clean and build
mvn clean install -DskipTests

# Build with tests
mvn clean install
```

#### 3. Run the application

```bash
# Run directly
mvn spring-boot:run

# Or run the JAR file
java -jar target/hexagonal-calculator-api-2.0.0.jar
```

#### 4. Access the API

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/api/v1/calculator/health

### Option 2: Run with Docker (Recommended)

#### 1. Clone the repository

```bash
git clone https://github.com/yourusername/hexagonal-calculator-api.git
cd hexagonal-calculator-api
```

#### 2. Build and run with Docker Compose

```bash
# Build and run
docker-compose up --build

# Run in background
docker-compose up -d --build

# View logs
docker-compose logs -f calculator-api

# Stop the application
docker-compose down
```

#### 3. Access the API

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/api/v1/calculator/health

## 📖 API Endpoints

### Base URL
```
http://localhost:8080/api/v1/calculator
```

### 1. Addition

**Endpoint**: `POST /api/v1/calculator/add`

**Parameters**:
- `operand1` (string, required): First number (e.g., "10.50")
- `operand2` (string, required): Second number (e.g., "5.25")

**Example Request**:
```bash
curl -X POST "http://localhost:8080/api/v1/calculator/add?operand1=10.50&operand2=5.25"
```

**Example Response**:
```json
{
  "operand1": 10.50,
  "operand2": 5.25,
  "operation": "+",
  "result": 15.75
}
```

### 2. Subtraction

**Endpoint**: `POST /api/v1/calculator/subtract`

**Parameters**:
- `operand1` (string, required): First number
- `operand2` (string, required): Second number

**Example Request**:
```bash
curl -X POST "http://localhost:8080/api/v1/calculator/subtract?operand1=20.50&operand2=5.25"
```

**Example Response**:
```json
{
  "operand1": 20.50,
  "operand2": 5.25,
  "operation": "-",
  "result": 15.25
}
```

### 3. Multiplication

**Endpoint**: `POST /api/v1/calculator/multiply`

**Parameters**:
- `operand1` (string, required): First number
- `operand2` (string, required): Second number

**Example Request**:
```bash
curl -X POST "http://localhost:8080/api/v1/calculator/multiply?operand1=10.50&operand2=2.00"
```

**Example Response**:
```json
{
  "operand1": 10.50,
  "operand2": 2.00,
  "operation": "*",
  "result": 21.00
}
```

### 4. Division

**Endpoint**: `POST /api/v1/calculator/divide`

**Parameters**:
- `operand1` (string, required): Dividend
- `operand2` (string, required): Divisor (cannot be zero)
- `scale` (integer, optional): Decimal places (default: 2)

**Example Request**:
```bash
curl -X POST "http://localhost:8080/api/v1/calculator/divide?operand1=10.50&operand2=2.00&scale=2"
```

**Example Response**:
```json
{
  "operand1": 10.50,
  "operand2": 2.00,
  "operation": "/",
  "result": 5.25
}
```

### 5. Health Check

**Endpoint**: `GET /api/v1/calculator/health`

**Example Request**:
```bash
curl http://localhost:8080/api/v1/calculator/health
```

**Example Response**:
```
Calculator API is running
```

## 🧪 Running Tests

### Run all tests

```bash
mvn test
```

### Run specific test class

```bash
mvn test -Dtest=CalculationTest
```

### Run with coverage report

```bash
mvn test jacoco:report
```

### Test Files

- **CalculationTest.java** - Unit tests for domain model
- **CalculatorUseCaseImplTest.java** - Unit tests for use cases with mocks
- **CalculatorControllerTest.java** - Integration tests for REST endpoints

## 🐳 Docker Deployment

### Using Docker Compose (Recommended)

```bash
# Build and start
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down

# Rebuild
docker-compose up -d --build
```

### Manual Docker Build

```bash
# Build image
docker build -t hexagonal-calculator-api:2.0.0 .

# Run container
docker run -d -p 8080:8080 --name calculator hexagonal-calculator-api:2.0.0

# View logs
docker logs -f calculator

# Stop container
docker stop calculator
docker rm calculator
```

## ⚙️ Configuration

### Application Properties

Edit `src/main/resources/application.yml`:

```yaml
server:
  port: 8080                    # Server port

logging:
  level:
    root: INFO                  # Root log level
    io.corp.calculator: DEBUG   # Application log level
```

### Environment Variables (Docker)

```bash
SPRING_PROFILES_ACTIVE=prod     # Active profile
SERVER_PORT=8080                # Server port
```

## 📊 BigDecimal Precision

All arithmetic operations use **BigDecimal** for exact decimal arithmetic:

```bash
# Precise calculation example
curl -X POST "http://localhost:8080/api/v1/calculator/add?operand1=0.1&operand2=0.2"

# Response: 0.3 (exactly, not 0.30000000000000004)
```

This ensures accurate calculations for financial applications.

## 🔍 Logging

The application logs important events to help with debugging:

```bash
# View logs (Maven)
mvn spring-boot:run

# View logs (Docker)
docker-compose logs -f calculator-api

# Configure log level
# Edit application.yml and set logging.level
```

## 📚 Architecture Benefits

✅ **Testable** - Business logic tested without HTTP or databases
✅ **Maintainable** - Clear separation of concerns
✅ **Scalable** - Easy to add new adapters or features
✅ **Flexible** - Swap implementations without touching core logic
✅ **Reusable** - Domain logic can be used anywhere

## 🔧 Development Guide

### Adding a New Operation

1. **Create Command class** in `domain/ports/input/`
2. **Add method to CalculatorInputPort interface**
3. **Implement in CalculatorUseCaseImpl**
4. **Add domain logic method to Calculation model**
5. **Add REST endpoint in CalculatorController**
6. **Write tests**

### Adding a New Output Adapter

1. **Create adapter class** implementing output port
2. **Add @Component annotation**
3. **Inject in use case constructor**

No need to modify domain logic!

## 🐛 Troubleshooting

### Port 8080 already in use

```bash
# Change port in application.yml
# Or kill the process using port 8080
lsof -i :8080
kill -9 <PID>
```

### Docker build fails

```bash
# Clean up and rebuild
docker-compose down
docker system prune
docker-compose up -d --build
```

### Tests fail

```bash
# Ensure Java 17+ is installed
java -version

# Clear Maven cache
mvn clean
mvn install
```

## 📝 License

Apache License 2.0

## 👤 Author

Calculator Team

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📞 Support

For issues and questions:
- Open an issue on GitHub
- Check existing documentation

## 🗺️ Roadmap

- [ ] Database persistence adapter
- [ ] Authentication/Authorization
- [ ] Rate limiting
- [ ] Caching layer
- [ ] GraphQL support
- [ ] Kubernetes deployment
- [ ] Metrics and monitoring

## 📋 Checklist for First Run

- [ ] Clone the repository
- [ ] Install Java 17+
- [ ] Run `mvn clean install` (Maven) OR `docker-compose up` (Docker)
- [ ] Access http://localhost:8080/swagger-ui.html
- [ ] Run tests: `mvn test`
- [ ] Try API endpoints using curl or Swagger UI

Enjoy using the Hexagonal Calculator API! 🎉
