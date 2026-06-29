# Testing Skill - Automated Test Execution & Verification

## Quick Reference Commands

### Run All Tests (Root Level)
```bash
mvn clean test
```
Executes all unit tests across all modules and generates JaCoCo coverage reports.

### Run Tests for Specific Module
```bash
mvn clean test -pl {module-name}
```
Example: `mvn clean test -pl acabou-mony-transaction`

### Run Specific Test Class
```bash
mvn test -Dtest={TestClassName}
```
Example: `mvn test -Dtest=TransacaoServiceTest`

### Run Specific Test Method
```bash
mvn test -Dtest={TestClassName}#{testMethodName}
```
Example: `mvn test -Dtest=TransacaoServiceTest#testProcessarTransacao`

### Generate Coverage Report
```bash
mvn clean test jacoco:report
```
Coverage report: `target/site/jacoco/index.html`

### Verify Coverage Threshold
```bash
mvn clean test jacoco:check
```
Fails build if coverage drops below 80% threshold.

---

## Module-Specific Test Workflows

### Transaction Module (`acabou-mony-transaction`)
```bash
# Run all transaction tests
mvn clean test -pl acabou-mony-transaction

# Run TransacaoService tests only
mvn test -Dtest=TransacaoServiceTest -pl acabou-mony-transaction

# Verify BigDecimal precision handling
mvn test -Dtest=TransacaoServiceTest#testProcessarTransacaoComValorPreciso -pl acabou-mony-transaction

# Verify RabbitMQ messaging (non-blocking)
mvn test -Dtest=TransacaoServiceTest#testPublicarNotificacao -pl acabou-mony-transaction

# Verify inter-service HTTP calls
mvn test -Dtest=TransacaoServiceTest#testValidarSaldoContaOrigemIndisponivel -pl acabou-mony-transaction
```

### Account Module (`acabou-mony-account`)
```bash
# Run all account tests
mvn clean test -pl acabou-mony-account

# Run UsuarioService tests
mvn test -Dtest=UsuarioServiceTest -pl acabou-mony-account

# Run ContaService tests
mvn test -Dtest=ContaServiceTest -pl acabou-mony-account
```

### Auth Module (`acabou-mony-auth`)
```bash
# Run all auth tests
mvn clean test -pl acabou-mony-auth

# Run AuthService tests
mvn test -Dtest=AuthServiceTest -pl acabou-mony-auth

# Run TwoFactorAuthService tests
mvn test -Dtest=TwoFactorAuthServiceTest -pl acabou-mony-auth
```

### Card Module (`acabou-mony-card`)
```bash
# Run all card tests
mvn clean test -pl acabou-mony-card

# Run CartaoService tests
mvn test -Dtest=CartaoServiceTest -pl acabou-mony-card
```

### Auditing Module (`acabou-mony-auditing`)
```bash
# Run all auditing tests
mvn clean test -pl acabou-mony-auditing

# Run AuditService tests
mvn test -Dtest=AuditServiceTest -pl acabou-mony-auditing
```

### Notification Module (`acabou-mony-notificacao`)
```bash
# Run all notification tests
mvn clean test -pl acabou-mony-notificacao

# Run RabbitMQ consumer tests
mvn test -Dtest=NotificacaoConsumerTest -pl acabou-mony-notificacao
```

### Gateway Module (`acabou-mony-gateway`)
```bash
# Run all gateway tests
mvn clean test -pl acabou-mony-gateway

# Run JWT filter tests
mvn test -Dtest=JwtWebFilterTest -pl acabou-mony-gateway
```

---

## Coverage Verification Workflow

### Step 1: Generate Coverage Report
```bash
mvn clean test jacoco:report
```

### Step 2: Check Coverage Threshold
```bash
mvn jacoco:check
```
Expected output: `BUILD SUCCESS` (if coverage ≥ 80%)

### Step 3: View Coverage Report
Open in browser: `target/site/jacoco/index.html`

### Step 4: Identify Uncovered Lines
- Navigate to specific class in report
- Red lines = uncovered code
- Yellow lines = partially covered code
- Green lines = fully covered code

### Step 5: Add Missing Tests
- Create test method in corresponding `*Test.java` class
- Follow AAA pattern (Arrange → Act → Assert)
- Mock external dependencies
- Verify method invocations

### Step 6: Re-run Coverage Check
```bash
mvn clean test jacoco:check
```

---

## Test Execution Patterns

### Pattern 1: Service Method Testing
```bash
# Test single service method
mvn test -Dtest=TransacaoServiceTest#testProcessarTransacao

# Test exception handling
mvn test -Dtest=TransacaoServiceTest#testProcessarTransacaoComSaldoInsuficiente

# Test validation
mvn test -Dtest=TransacaoServiceTest#testProcessarTransacaoComValorInvalido
```

### Pattern 2: Integration Testing (Controller + Service)
```bash
# Run controller integration tests (MockMvc)
mvn test -Dtest=TransacaoControllerTest

# Test specific endpoint
mvn test -Dtest=TransacaoControllerTest#testCriarTransacao
```

### Pattern 3: Dependency Mocking
```bash
# Test with mocked repository
mvn test -Dtest=TransacaoServiceTest#testObterTransacao

# Test with mocked HTTP client
mvn test -Dtest=TransacaoServiceTest#testValidarSaldoContaOrigemIndisponivel

# Test with mocked RabbitTemplate
mvn test -Dtest=TransacaoServiceTest#testPublicarNotificacao
```

---

## Continuous Integration Workflow

### Full Build & Test Pipeline
```bash
# Clean, compile, run tests, verify coverage, package
mvn clean verify
```

### Build with Strict Coverage Enforcement
```bash
# Fails if coverage < 80%
mvn clean test jacoco:check
```

### Skip Tests (Use Cautiously)
```bash
mvn clean package -DskipTests
```

### Run Tests in Parallel (Faster Execution)
```bash
mvn clean test -T 1C
```
`-T 1C` = 1 thread per core

---

## Debugging Test Failures

### Enable Debug Logging
```bash
mvn test -X
```
Verbose output with debug information.

### Run Single Test with Debug
```bash
mvn test -Dtest=TransacaoServiceTest#testProcessarTransacao -X
```

### View Test Output
```bash
cat target/surefire-reports/com.example.acabou_mony_transaction.service.TransacaoServiceTest.txt
```

### Check for Flaky Tests
```bash
# Run test 10 times
mvn test -Dtest=TransacaoServiceTest#testProcessarTransacao -Drepeat=10
```

---

## Code Coverage Exclusions

### Exclude Class from Coverage
```java
@Generated(value = "Lombok", date = "2024-01-01")
public class MyEntity {
    // Lombok-generated code
}
```

### Exclude Method from Coverage
```java
@Generated
public String toString() {
    return super.toString();
}
```

---

## Performance Testing

### Measure Test Execution Time
```bash
mvn clean test -DargLine="-Xmx512m"
```

### Profile Slow Tests
```bash
mvn test -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
```

---

## Validation Checklist

- [ ] All tests pass: `mvn clean test`
- [ ] Coverage ≥ 80%: `mvn jacoco:check`
- [ ] No compilation errors: `mvn clean compile`
- [ ] No test failures: Check `target/surefire-reports/`
- [ ] Service classes have tests: Verify `*ServiceTest.java` exists
- [ ] Mocks are properly configured: Verify `@Mock` and `@InjectMocks`
- [ ] AAA pattern followed: Arrange → Act → Assert
- [ ] Exception scenarios tested: Verify `@Test(expected = ...)` or `assertThrows()`
- [ ] Coverage report generated: Check `target/site/jacoco/index.html`

---

## Troubleshooting

### Issue: Tests Pass Locally but Fail in CI
**Solution**: Ensure all dependencies are mocked, no hardcoded paths, use `@TempDir` for file operations.

### Issue: Coverage Report Not Generated
**Solution**: Verify JaCoCo plugin in `pom.xml`, run `mvn clean test jacoco:report`.

### Issue: Flaky Tests
**Solution**: Add `@Timeout`, use `Thread.sleep()` sparingly, mock time-dependent code.

### Issue: Memory Issues During Tests
**Solution**: Increase heap: `mvn test -DargLine="-Xmx1024m"`

---

**Last Updated**: 2024
**Scope**: Automated test execution and verification only
