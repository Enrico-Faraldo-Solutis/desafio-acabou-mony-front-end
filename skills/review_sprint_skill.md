# Review Sprint Skill

## Objective
Analyze the most recent sprint task file and compare against the current project state to identify completed tasks, partial implementations, and blockers. Update the corresponding progress file with accurate status summary.

## Scope
- **Review Only**: No coding, testing, or planning activities
- **Analysis Focus**: Sprint completion status, task fulfillment, implementation quality
- **Output**: Updated progress file reflecting current state

---

## Workflow

### Phase 1: Sprint File Analysis
1. Locate the most recent `sprint-x.md` file in the `sprints/` folder
2. Extract all task descriptions and acceptance criteria
3. Identify module(s) affected by the sprint
4. Document expected deliverables and success metrics

### Phase 2: Project State Inspection
1. Examine source code in affected modules (`src/main/java/`)
2. Check for implementation of each sprint task
3. Verify code structure matches expected architecture
4. Review configuration files and dependencies
5. Inspect database schema alignment with requirements

### Phase 3: Completion Assessment
For each sprint task, determine status:
- **✅ COMPLETED**: Code implemented, follows standards, meets acceptance criteria
- **⚠️ PARTIAL**: Code exists but incomplete, has issues, or partially meets criteria
- **❌ NOT STARTED**: No code implementation found
- **🔄 NEEDS REVIEW**: Implementation exists but quality/correctness unclear

Document specific findings:
- What was implemented vs. what was required
- Code quality issues or deviations from standards
- Missing features or incomplete implementations
- Configuration or dependency problems

### Phase 4: Progress File Update
Update the corresponding `progress_{module}.md` file with:

1. **Current Status**: One-line summary of overall sprint completion
2. **Completed Features**: List all ✅ tasks with brief descriptions
3. **Partial Implementations**: List ⚠️ tasks with specific issues noted
4. **Not Started**: List ❌ tasks (if any)
5. **Known Limitations**: Document blockers, missing features, or constraints
6. **Next Steps**: Prioritized list of remaining work
7. **Last Updated**: Current date

---

## Commands & Tools

### Locate Sprint File
```bash
ls -la sprints/ | grep sprint
```

### Inspect Module Structure
```bash
find {module}/src/main/java -type f -name "*.java" | head -20
```

### Review Key Files
- Service classes: `src/main/java/com/example/{module}/service/`
- Controllers: `src/main/java/com/example/{module}/controller/`
- Entities: `src/main/java/com/example/{module}/entity/`
- DTOs: `src/main/java/com/example/{module}/dto/`
- Configuration: `src/main/resources/application.properties`

### Check Dependencies
```bash
cat {module}/pom.xml | grep -A 5 "<dependency>"
```

---

## Assessment Criteria

### Code Completeness
- [ ] All required classes exist (Service, Controller, Entity, DTO, Mapper)
- [ ] Methods implement expected functionality
- [ ] Database schema matches entity definitions
- [ ] Configuration properties are set correctly

### Standards Compliance
- [ ] Code follows naming conventions (PascalCase, camelCase, UPPER_SNAKE_CASE)
- [ ] Proper use of Spring annotations (@Service, @Repository, @Entity, etc.)
- [ ] Lombok annotations applied (@Data, @Builder, @Slf4j)
- [ ] Constructor injection used (not field injection)
- [ ] Logging present in service methods

### Functional Requirements
- [ ] All sprint acceptance criteria met
- [ ] API endpoints match specification
- [ ] Request/response DTOs properly structured
- [ ] Error handling implemented
- [ ] Business logic correctly implemented

### Integration Points
- [ ] Inter-service communication configured (if applicable)
- [ ] RabbitMQ messaging setup (if applicable)
- [ ] Database connectivity verified
- [ ] External service clients configured

---

## Progress File Template

```markdown
# Progress: {Module Name}

## Current Status
{One-line summary of sprint completion}

## Module Overview
- **Port**: {port}
- **Purpose**: {brief description}
- **Assigned Developer**: {name}
- **Sprint**: {sprint-x}

## Completed Features
- ✅ {Feature 1}: {brief description}
- ✅ {Feature 2}: {brief description}

## Partial Implementations
- ⚠️ {Feature}: {specific issue or missing part}

## Known Limitations
- {Limitation 1}
- {Limitation 2}

## Next Steps
1. {Priority 1 task}
2. {Priority 2 task}

## Dependencies
- **Depends On**: {service1, service2}
- **Consumed By**: {service3, service4}

## Last Updated
{Date}
```

---

## Review Checklist

- [ ] Sprint file located and analyzed
- [ ] All tasks reviewed against current code
- [ ] Completion status determined for each task
- [ ] Issues and blockers documented
- [ ] Progress file updated with findings
- [ ] File respects 200-line limit
- [ ] Status indicators (✅ ⚠️ ❌ 🔄) used consistently
- [ ] Next steps prioritized and actionable

---

## Output Deliverable

**File**: `progress_{module}.md`
- **Location**: Project root
- **Format**: Markdown
- **Max Lines**: 200
- **Content**: Accurate reflection of current sprint completion status
- **Indicators**: Clear status symbols for each item
- **Actionability**: Next steps are specific and prioritized

---

## Notes

- Review is **read-only**: No modifications to source code
- Focus on **factual assessment**: What exists vs. what was required
- Document **specific issues**: Not vague observations
- Prioritize **next steps**: Most critical work first
- Update **only progress file**: No other files modified
- Maintain **consistency**: Use same format across all progress files
