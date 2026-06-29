# Sprint Planning Skill

## Objective
Plan and document sprints by analyzing current progress, defining sprint objectives, breaking down tasks, and generating a single sprint markdown file and no other files.

---

## Sprint Planning Workflow

### Step 1: Analyze Current Module Progress
**Input**: Read `progress_{module}.md` (e.g., `progress_transaction.md`)

**Actions**:
- Identify "Current Status" section
- Review "Completed Features" checklist
- Note "Known Limitations" and blockers
- Extract "Next Steps" priority list
- Check "Last Updated" date for staleness

**Output**: Module state assessment and priority list

### Step 2: Review Story Alignment
**Input**: Cross-reference `enunciado.md` user stories with progress file

**Actions**:
- Map completed features to story acceptance criteria
- Identify partially-implemented stories
- Flag stories with blockers or dependencies
- Note security/performance requirements

**Output**: Sprint scope definition and story coverage gaps

### Step 3: Define Sprint Objectives
**Input**: Prioritize "Next Steps" from progress file

**Actions**:
- Select top 3-5 items from priority list
- Verify dependencies are satisfied
- Estimate effort (small/medium/large)
- Check for blockers or external dependencies
- Confirm alignment with story requirements

**Output**: Sprint goal and task breakdown

---

## Sprint Markdown Generation Workflow

### Step 4: Create Sprint File
**Output File**: `sprints/sprint-X.md` (where X is sprint number)

**File Structure** (max 300 lines):

```markdown
# Sprint X - [Sprint Goal]

## Sprint Objectives
- [ ] Objective 1 (Story reference)
- [ ] Objective 2 (Story reference)
- [ ] Objective 3 (Story reference)

## Tasks Breakdown

### Task 1: [Feature Name]
- **Story**: Story X - [Story Title]
- **Module**: [module-name]
- **Acceptance Criteria**:
  - Criterion 1
  - Criterion 2
- **Implementation Details**:
  - File: `path/to/file.java`
  - Changes: Brief description
- **Dependencies**: [List blocking tasks]
- **Status**: NOT STARTED

### Task 2: [Feature Name]
[Same structure as Task 1]

## Progress Tracking

| Task | Status | Completion % |
|------|--------|-------------|
| Task 1 | NOT STARTED | 0% |
| Task 2 | NOT STARTED | 0% |

## Blockers & Risks
- [List any blockers]
- [List any risks]

## Notes
- [Planning notes]
- [Design decisions]
```

**Mandatory Sections**:
- Sprint Objectives (linked to stories)
- Tasks Breakdown (with acceptance criteria and dependencies)
- Progress Tracking table
- Blockers & Risks section
- Notes section

**Guidelines**:
- Keep file under 300 lines
- Use checkboxes for objectives (unchecked initially)
- Set all task statuses to "NOT STARTED"
- Set all completion percentages to 0%
- Include story references for traceability
- List module assignments for each task
- Document known blockers and risks upfront

### Step 5: Document Task Dependencies
**Input**: Task breakdown from Step 3

**Actions**:
- Identify inter-task dependencies
- Map external service dependencies (e.g., account-service, RabbitMQ)
- Note any module dependencies
- Flag tasks that can run in parallel
- Identify critical path items

**Output**: Dependency graph in sprint file

### Step 6: Identify Blockers & Risks
**Input**: Progress file limitations and external factors

**Actions**:
- List any known blockers preventing task execution
- Identify external service dependencies
- Note missing information or unclear requirements
- Document infrastructure constraints
- Flag integration points with other modules

**Output**: Blockers & Risks section in sprint file

---

## Sprint Planning Checklist

Before finalizing sprint file, verify:

- [ ] Sprint goal is clear and measurable
- [ ] Objectives are linked to user stories
- [ ] All tasks have acceptance criteria defined
- [ ] Task dependencies are documented
- [ ] Module assignments are specified
- [ ] Blockers and risks are identified
- [ ] Sprint file is under 300 lines
- [ ] All mandatory sections are present
- [ ] Status indicators are consistent (NOT STARTED, IN PROGRESS, COMPLETE)
- [ ] File is saved as `sprints/sprint-X.md`

---

## Planning Example

**Input**: Analyzing `progress_transaction.md`

**Step 1-3 Output**:
- Current Status: IMPLEMENTATION COMPLETE
- Next Steps: JaCoCo plugin, Circuit Breaker, Distributed Tracing
- Selected for Sprint 1: Top 2 items (JaCoCo, Circuit Breaker)

**Step 4-6 Output**: `sprints/sprint-1.md`
```markdown
# Sprint 1 - Code Coverage & Resilience

## Sprint Objectives
- [ ] Configure JaCoCo Maven plugin with 80% threshold (Story 1)
- [ ] Implement circuit breaker for AccountClient (Story 2)

## Tasks Breakdown

### Task 1: Configure JaCoCo Maven Plugin
- **Story**: Story 1 - Transaction Processing
- **Module**: acabou-mony-transaction
- **Acceptance Criteria**:
  - JaCoCo plugin configured in pom.xml
  - Coverage reports generated in target/site/jacoco/
  - Build fails if coverage < 80%
  - Service classes excluded: Entities, DTOs, Mappers
- **Implementation Details**:
  - File: `acabou-mony-transaction/pom.xml`
  - Changes: Add jacoco-maven-plugin with execution goals
- **Dependencies**: None
- **Status**: NOT STARTED

### Task 2: Add Resilience4j Circuit Breaker
- **Story**: Story 2 - Scalability
- **Module**: acabou-mony-transaction
- **Acceptance Criteria**:
  - Circuit breaker wraps AccountClient HTTP calls
  - Fallback behavior defined for open circuit
  - Metrics exposed for monitoring
- **Implementation Details**:
  - File: `AccountClient.java`
  - Changes: Add @CircuitBreaker annotation
- **Dependencies**: Task 1 (optional - for testing)
- **Status**: NOT STARTED

## Progress Tracking

| Task | Status | Completion % |
|------|--------|-------------|
| JaCoCo Plugin | NOT STARTED | 0% |
| Circuit Breaker | NOT STARTED | 0% |

## Blockers & Risks
- Resilience4j dependency version compatibility with Spring Boot 3.5.14
- JaCoCo report generation may require additional Maven configuration

## Notes
- Circuit breaker should use default settings (3 failures, 30s timeout)
- Consider adding metrics dashboard integration in future sprint
```

---

**Last Updated**: 2024
**Version**: 1.0
