# Coding Skill: Acabou-Mony Development

## Overview
This skill provides the workflow and commands for implementing coding tasks in the Acabou-Mony microservices project. The agent must analyze the most recent `sprint-x.md` file in the `sprints/` folder and complete one sprint task at a time.

**Important**: This skill covers CODING ONLY. Do NOT test, plan, review, or update progress files.

---

## Workflow

### 1. Sprint Task Analysis
**Objective**: Identify and understand the current sprint task

**Steps**:
1. Read the most recent `sprint-x.md` file from the `sprints/` folder
2. Identify the first incomplete task (marked with ❌ or ⚠️)
3. Extract task requirements, acceptance criteria, and affected modules
4. Note any dependencies on other services or modules
5. Identify the target module and file paths

**Command**:
```bash
# Read the sprint file to understand current task
cat sprints/sprint-x.md
```

---

### 2. Code Implementation
**Objective**: Write production-ready code following project standards

**Steps**:
1. Navigate to the target module directory
2. Create or modify files according to task requirements
3. Follow naming conventions: PascalCase (classes), camelCase (methods/variables)
4. Use Lombok annotations: `@Data`, `@Bu@Repository`
5. Apply Spring annotations: `@Transactional`, `@RestController`, `@Entity`
6. Implement error handling with custom exceptions
7. Add logging with `@Slf4j` and appropriate log levels (INFO, WARN, ERROR)
8. Use constructor injection (not field injection)
9. For monetary vilder`, `@Slf4j`, `@Service`, `alues: use `java.math.BigDecimal` with `@Column(precision = 15, scale = 2)`
10. For transaction status: use enum state machine (PENDENTE → CONCLUIDA/FALHA)

**Key Implementation Areas**:

#### Service Layer
- Implement business logic with `@Service` annotation
- Add `@Transactional` on methods that modify state
- Use `@Transactional(readOnly = true)` for query-only methods
- Include comprehensive logging with context (IDs, amounts, user info)
- Throw domain-specific exceptions for error conditions
- For RabbitMQ: wrap messaging in try-catch, log errors, do NOT rethrow

#### Controller Layer
- Use `@RestController` and `@RequestMapping` for REST endpoints
- Add `@Valid` on method parameters for validation
- Use `@Validated` on class level
- Return appropriate HTTP status codes (200, 201, 400, 404, 500)
- Map service exceptions to HTTP responses

#### Entity Layer
- Use `@Entity` and `@Table` annotations
- Use `@Data` for Lombok-generated getters/setters
- Add `@Column` with constraints (nullable, precision, scale)
- Use `@Enumerated(EnumType.STRING)` for enum fields
- Add `@PrePersist` for default values (e.g., transaction status)

#### DTO Layer
- Use `@Data` and `@Builder` for DTOs
- Add validation annotations: `@NotNull`, `@DecimalMin`, `@Positive`, `@Size`, `@Email`
- Include meaningful validation messages
- Use `@JsonProperty` for JSON field mapping if needed

#### Repository Layer
- Extend `JpaRepository<Entity, ID>`
- Use derived query methods for simple lookups
- Use `@Query` annotations for complex queries
- Add pagination support with `Page<T>` and `Pageable`

#### Client Layer (Inter-service Communication)
- Use `RestTemplate` for HTTP calls
- Set connection timeout: 5 seconds
- Set read timeout: 10 seconds
- Catch `RestClientException` and convert to domain exceptions
- Log all inter-service calls with request/response details

#### Configuration Layer
- Configure RabbitMQ: Exchange, Queue, Binding, RoutingKey
- Use `Jackson2JsonMessageConverter` for message serialization
- Register `JavaTimeModule` in ObjectMapper for LocalDateTime support
- Configure connection pools and timeouts

---

### 3. Code Verification
**Objective**: Ensure code compiles and follows standards

**Commands**:
```bash
# Navigate to module directory
cd {module-name}

# Compile the module
mvn clean compile

# Check for compilation errors
mvn clean verify -DskipTests

# Verify code follows standards (if SonarQube available)
mvn sonar:sonar
```

**Verification Checklist**:
- ✅ Code compiles without errors
- ✅ No hardcoded credentials or secrets
- ✅ All classes follow naming conventions
- ✅ Logging includes sufficient context
- ✅ Error handling is comprehensive
- ✅ Validation annotations present on DTOs
- ✅ Transactional boundaries correctly placed
- ✅ BigDecimal used for monetary values
- ✅ Constructor injection used (not field injection)
- ✅ Lombok annotations applied correctly

---

## Implementation Commands

### Create New Java Class
```bash
# Service class
touch {module}/src/main/java/com/example/{module}/service/{ServiceName}Service.java

# Controller class
touch {module}/src/main/java/com/example/{module}/controller/{ControllerName}Controller.java

# Entity class
touch {module}/src/main/java/com/example/{module}/entity/{EntityName}.java

# DTO class
touch {module}/src/main/java/com/example/{module}/dto/{DtoName}Dto.java

# Repository interface
touch {module}/src/main/java/com/example/{module}/repository/{EntityName}Repository.java

# Exception class
touch {module}/src/main/java/com/example/{module}/exception/{ExceptionName}Exception.java
```

### Modify Configuration Files
```bash
# Update application.properties
nano {module}/src/main/resources/application.properties

# Update pom.xml for dependencies
nano {module}/pom.xml
```

### Build and Verify
```bash
# Build entire project
mvn clean install -DskipTests

# Build specific module
mvn clean install -DskipTests -pl {module-name}

# Check for compilation errors only
mvn clean compile
```

---

## Code Templates

### Service Class Template
```java
@Service
@Slf4j
@RequiredArgsConstructor
public class {ServiceName}Service {
    
    private final {Repository}Repository repository;
    private final {Mapper}Mapper mapper;
    
    @Transactional
    public {ResponseDto} create({RequestDto} dto) {
        log.info("Creating {entity}: {}", dto);
        
        {Entity} entity = mapper.toEntity(dto);
        {Entity} saved = repository.save(entity);
        
        log.info("{entity} created successfully: id={}", saved.getId());
        return mapper.toDto(saved);
    }
    
    @Transactional(readOnly = true)
    public {ResponseDto} getById(Long id) {
        log.info("Fetching {entity}: id={}", id);
        
        {Entity} entity = repository.findById(id)
            .orElseThrow(() -> new {EntityName}NotFoundException("Not found"));
        
        return mapper.toDto(entity);
    }
}
```

### Controller Class Template
```java
@RestController
@RequestMapping("/api/{endpoint}")
@Slf4j
@RequiredArgsConstructor
@Validated
public class {ControllerName}Controller {
    
    private final {ServiceName}Service service;
    
    @PostMapping
    public ResponseEntity<{ResponseDto}> create(@Valid @RequestBody {RequestDto} dto) {
        log.info("POST request: {}", dto);
        {ResponseDto} response = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<{ResponseDto}> getById(@PathVariable Long id) {
        log.info("GET request: id={}", id);
        {ResponseDto} response = service.getById(id);
        return ResponseEntity.ok(response);
    }
}
```

### Entity Class Template
```java
@Entity
@Table(name = "{table_name}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class {EntityName} {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    
    @PrePersist
    protected void onCreate() {
        this.status = Status.PENDENTE;
    }
}
```

### DTO Class Template
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class {DtoName}Dto {
    
    @NotNull(message = "Field is required")
    private String name;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;
    
    @Email(message = "Invalid email format")
    private String email;
}
```

---

## Task Completion Criteria

A sprint task is complete when:

1. **Code is written** following all project standards
2. **Code compiles** without errors (`mvn clean compile`)
3. **All files created/modified** as specified in task requirements
4. **Naming conventions** applied correctly (PascalCase, camelCase, UPPER_SNAKE_CASE)
5. **Annotations applied** correctly (Lombok, Spring, JPA, Validation)
6. **Error handling** implemented with custom exceptions
7. **Logging** added with appropriate context and log levels
8. **Validation** annotations present on DTOs
9. **Transactional boundaries** correctly placed
10. **Inter-service communication** includes timeout and error handling
11. **RabbitMQ messaging** wrapped in try-catch without rethrowing
12. **BigDecimal** used for all monetary values
13. **Constructor injection** used (not field injection)

---

## Common Pitfalls to Avoid

❌ **Do NOT**:
- Use `float` or `double` for monetary values (use `BigDecimal`)
- Use field injection with `@Autowired` (use constructor injection)
- Rethrow RabbitMQ messaging exceptions (log and continue)
- Hardcode credentials or secrets in code
- Skip logging in service methods
- Use `equals()` for BigDecimal comparisons (use `compareTo()`)
- Forget `@Transactional` on state-modifying methods
- Skip validation annotations on DTO fields
- Use `@Query` for simple derived queries (use method names)
- Forget error handling in inter-service HTTP calls

✅ **Do**:
- Use `BigDecimal` for all monetary values
- Use constructor injection with `@RequiredArgsConstructor`
- Log entry, exit, and error conditions in services
- Add validation annotations to all DTOs
- Use `@Transactional` on service methods that modify state
- Implement comprehensive error handling
- Include context in log messages (IDs, amounts, user info)
- Use `compareTo()` for BigDecimal comparisons
- Wrap RabbitMQ calls in try-catch
- Set timeouts for inter-service HTTP calls

---

## Sprint Task Execution Flow

1. **Read** the most recent `sprint-x.md` file
2. **Identify** the first incomplete task
3. **Analyze** requirements and affected modules
4. **Implement** code following templates and standards
5. **Verify** code compiles with `mvn clean compile`
6. **Confirm** all task acceptance criteria are met
7. **Move to next task** in the same sprint

**Do NOT update progress files or markdown documentation.**

---

**Last Updated**: 2024
**Skill Type**: Coding Implementation
**Scope**: Code writing only (no testing, planning, or reviewing)
