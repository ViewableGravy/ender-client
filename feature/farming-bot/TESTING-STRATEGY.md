# Testing Strategy - Farming Bot Feature

## Overview
This document outlines the testing approach for the Farming Bot feature, covering manual testing procedures, automated testing opportunities, and quality assurance guidelines.

## Testing Levels

### 1. Unit Testing (Automated)
**Scope**: Individual classes and methods  
**Framework**: JUnit 4  
**Coverage Target**: >80% for model and utility classes  

**What to test**:
- Data model classes (FarmField, FieldGrid, TileState, etc.)
- Utility functions (coordinate conversion, boundary checking)
- Configuration serialization/deserialization
- Enum validations

**Example**:
```java
@Test
public void testFieldGridContainsBoundary() {
    FieldGrid grid = FieldGrid.rectangle(0, 0, 10, 10);
    assertTrue(grid.contains(new Coord2d(0, 0)));   // Corner
    assertTrue(grid.contains(new Coord2d(5, 5)));   // Center
    assertFalse(grid.contains(new Coord2d(11, 11))); // Outside
}
```

**Run with**: `ant test` (after adding test target to build.xml)

---

### 2. Integration Testing (Manual + Automated)
**Scope**: Component interactions  
**Focus**: UI ↔ Model, Bot ↔ Game, Overlay ↔ MapView  

**Manual integration tests**:
- Field creation UI → FarmingManager → Config persistence
- Overlay rendering → Field data → MapView
- Bot state machine → Tile operations → Inventory management

**Semi-automated** (test helpers):
```java
public class TestHelpers {
    public static FarmField createTestField(String name) {
        FarmField field = new FarmField(name, FieldType.QUANTITY, FieldShape.RECTANGLE);
        field.setGrid(FieldGrid.rectangle(100, 200, 110, 210));
        field.setCropType("gfx/terobjs/plants/wheat");
        return field;
    }
    
    public static void populateTestFields(FarmingManager manager, int count) {
        for (int i = 0; i < count; i++) {
            manager.addField(createTestField("Test Field " + i));
        }
    }
}
```

---

### 3. System Testing (Manual)
**Scope**: End-to-end workflows  
**Environment**: Live game client + test world  

**Test scenarios documented per epic** (see sections below).

---

### 4. User Acceptance Testing (UAT)
**Scope**: Real-world usage by stakeholders  
**Timeline**: After Epic 6 complete (basic bot working)  
**Duration**: 1-2 weeks  

**UAT checklist**:
- [ ] Bot can farm a field autonomously for 24+ hours
- [ ] Quality breeding shows measurable improvement
- [ ] Multiple fields work simultaneously
- [ ] UI is intuitive (non-technical user can configure)
- [ ] Performance is acceptable (no lag, no crashes)

---

## Manual Testing Procedures by Epic

### Epic 1: Field Data Structures
**Goal**: Verify data models work correctly  

**Test cases**:
1. **Field Creation**
   - Create field with all property combinations
   - Verify UUID uniqueness
   - Test validation (null name, null type)
   - Expected: Fields created successfully with valid data, errors with invalid data

2. **Grid Boundaries**
   - Create rectangular field (10x10)
   - Create circular field (radius 5)
   - Test `contains()` at boundaries
   - Expected: Correct boundary detection

3. **Serialization**
   - Create field, save to JSON
   - Reload client, verify field persists
   - Edit field, save, reload
   - Expected: Data persists correctly across restarts

**Pass criteria**: All test cases pass, no exceptions

---

### Epic 2: Custom Overlay System
**Goal**: Verify overlay renders correctly  

**Test cases**:
1. **Overlay Visibility**
   - Toggle overlay on/off with hotkey
   - Expected: Overlay appears/disappears instantly

2. **Field Rendering**
   - Create 3 fields (rectangle, circle, quality vs quantity)
   - Expected: All fields visible with correct colors

3. **Performance**
   - Create 10 fields with 100+ tiles each
   - Move camera, zoom in/out
   - Expected: Smooth 60 FPS, no lag

**Test environment**:
- Test world with flat terrain
- Use `/dev` console for coordinate testing

**Pass criteria**: Overlay renders correctly, no performance issues

---

### Epic 3: Field Editor UI
**Goal**: Verify UI interactions work  

**Test cases**:
1. **Field Creation Wizard**
   - Open wizard, create rectangular field
   - Click two corners on map
   - Name field "Test Farm", select crop "Wheat"
   - Expected: Field created and appears in list

2. **Field Editing**
   - Edit existing field name
   - Change crop type
   - Disable/enable field
   - Expected: Changes save and reflect in UI

3. **Field Deletion**
   - Delete field with confirmation
   - Cancel deletion
   - Expected: Field deleted only when confirmed

4. **Field List**
   - Create 10 fields
   - Sort by name, type, status
   - Filter by enabled/disabled
   - Expected: List updates correctly

**Usability testing**:
- Can a new user create a field in <2 minutes?
- Are error messages clear?
- Is the UI layout intuitive?

**Pass criteria**: All workflows functional, UI responsive

---

### Epic 4: Tile Detection & Basic Actions
**Goal**: Verify bot can interact with world  

**Test cases**:
1. **Tile Scanning**
   - Create field, run tile scan
   - Verify all tiles detected
   - Expected: Tile count matches grid size

2. **Tilling Action**
   - Select field, click "Till Field"
   - Observe bot behavior
   - Expected: Bot walks to field, tills all untilled tiles

3. **Planting Action**
   - Provide seeds in inventory
   - Click "Plant Field"
   - Expected: Bot plants all tilled tiles

4. **Harvesting Action**
   - Wait for crops to grow (or use creative mode)
   - Click "Harvest Field"
   - Expected: Bot harvests all mature crops

**Test data needed**:
- Seeds in inventory (10+ wheat seeds)
- Empty field (tilled or untilled)
- Test world with accessible terrain

**Pass criteria**: All basic actions work, bot doesn't get stuck

---

### Epic 5: Quantity Field Bot
**Goal**: Verify autonomous quantity farming  

**Test cases**:
1. **Full Automation**
   - Create quantity field (wheat)
   - Link input chest (seeds) and output chest (harvest)
   - Enable bot
   - Let run for 30 minutes
   - Expected: Bot completes till → plant → harvest cycle without intervention

2. **Quality Tolerance**
   - Plant crops with quality range -4 to +5
   - Verify bot doesn't skip low-quality crops
   - Expected: All crops harvested regardless of quality

3. **Error Handling**
   - Remove seeds mid-cycle
   - Block path to chest
   - Expected: Bot pauses with clear error message

4. **Multi-Cycle**
   - Let bot run for 2 hours (multiple harvest cycles)
   - Expected: Bot continues indefinitely, output chest fills

**Metrics to track**:
- Cycles per hour
- Harvest yield per cycle
- Errors encountered
- Uptime percentage

**Pass criteria**: Bot runs for 2+ hours without manual intervention, >90% uptime

---

### Epic 6: Quality Field Bot
**Goal**: Verify quality breeding logic  

**Test cases**:
1. **Quality Improvement**
   - Create circular quality field (radius 5)
   - Start with quality 0 seeds
   - Run bot for 10 cycles
   - Expected: Quality increases each cycle (measure final quality)

2. **Quality Filtering**
   - Observe bot discarding low-quality crops
   - Verify only +0 to +5 crops are replanted
   - Expected: Quality improves over generations

3. **Subdivision Breeding**
   - Create circle with 5 subdivisions
   - Plant different crops in each segment
   - Expected: Each segment breeds independently

**Metrics to track**:
- Quality progression (cycle 1, 5, 10)
- Discard rate (low-quality crops)
- Time per breeding cycle

**Pass criteria**: Quality increases by 10+ points over 10 cycles

---

### Epic 7: Multi-Field Orchestration
**Goal**: Verify scheduler manages multiple fields  

**Test cases**:
1. **Parallel Farming**
   - Create 5 fields (3 quantity, 2 quality)
   - Enable all, start bot
   - Expected: Bot switches between fields intelligently

2. **Priority Handling**
   - Set field priorities (P1, P2, P3)
   - Observe bot prioritizes correctly
   - Expected: Higher priority fields serviced first

3. **Resource Contention**
   - Two fields share same seed chest
   - Expected: Bot handles without deadlock

**Pass criteria**: Bot manages 5+ fields simultaneously, no deadlock

---

### Epic 8: Polish, Monitoring & External
**Goal**: Verify statistics, UI polish, and external monitoring  

**Test cases**:
1. **Statistics Dashboard**
   - Run bot for 1 hour
   - Check stats: crops planted, harvested, errors
   - Expected: Stats accurate

2. **Notifications**
   - Trigger error (remove seeds)
   - Expected: Notification appears

3. **External API** (if implemented)
   - Query `/api/fields` endpoint
   - Expected: JSON response with field data

**Pass criteria**: All polish features work, stats are accurate

---

## Automated Testing Opportunities

### What CAN be automated:
1. **Unit tests**: All model classes
2. **JSON serialization tests**: Save/load cycles
3. **Grid calculations**: Boundary checks, subdivisions
4. **State machine logic**: Bot state transitions

### What CANNOT be easily automated:
1. **Game interactions**: Clicking Gobs, pathfinding
2. **Visual verification**: Overlay rendering, colors
3. **Long-running behavior**: 24-hour bot runs
4. **Performance testing**: FPS, lag under load

### Recommended approach:
- **Automate**: Unit tests for all logic (80%+ coverage)
- **Manual**: Integration and system tests for game interactions
- **Semi-automated**: Test helpers for common scenarios (create test fields, populate data)

---

## Test Data & Fixtures

### Test Configuration
Create `etc/farming-test.json5` with pre-defined fields:

```json5
{
  "version": 1,
  "fields": [
    {
      "id": "test-field-1",
      "name": "Test Quantity Field",
      "type": "QUANTITY",
      "shape": "RECTANGLE",
      "grid": {"minX": 0, "minY": 0, "maxX": 10, "maxY": 10},
      "cropType": "gfx/terobjs/plants/wheat",
      "enabled": true
    },
    {
      "id": "test-field-2",
      "name": "Test Quality Field",
      "type": "QUALITY",
      "shape": "CIRCLE",
      "grid": {"centerX": 50, "centerY": 50, "radius": 5},
      "cropType": "gfx/terobjs/plants/carrot",
      "enabled": true
    }
  ]
}
```

### Test Helper Class
```java
public class FarmingTestUtils {
    public static void loadTestConfig() {
        // Load etc/farming-test.json5 instead of user config
    }
    
    public static void resetAllFields() {
        FarmingManager.getInstance().getAllFields().clear();
    }
    
    public static void createStandardTestFields() {
        // Create 5 fields with known properties
    }
}
```

---

## Performance Testing

### Benchmarks to track:
- **Overlay rendering**: Target 60 FPS with 10 fields visible
- **Tile scanning**: Scan 1000 tiles in <1 second
- **Config save/load**: <100ms for 50 fields
- **Bot action rate**: 1 action per 2-3 seconds (game limited)

### How to measure:
```java
long start = System.currentTimeMillis();
// ... operation ...
long duration = System.currentTimeMillis() - start;
System.out.println("Operation took: " + duration + "ms");
```

---

## Regression Testing

### When to run full regression:
- Before merging to master
- After Epic 6 complete (major milestone)
- Before each release

### Regression test suite:
1. All Epic test cases (abbreviated)
2. Focus on critical paths: field creation, bot automation, config persistence
3. Performance benchmarks

---

## Bug Reporting Template

When filing bugs during testing:

```markdown
**Bug ID**: FB-BUG-XXX
**Epic**: Epic X
**Story**: FB-X.Y
**Severity**: Critical / High / Medium / Low

**Description**: Brief summary

**Steps to Reproduce**:
1. Step 1
2. Step 2
3. Step 3

**Expected**: What should happen
**Actual**: What actually happens

**Environment**:
- Client version: ender-beta vX.X.X
- Java version: 1.8.0_XXX
- OS: Windows 10 / Linux / macOS

**Logs**: (Paste relevant error logs)

**Screenshots**: (Attach if applicable)
```

---

## Quality Gates

### Per Story
- [ ] Code compiles without errors
- [ ] No new compiler warnings
- [ ] Manual test cases pass
- [ ] Acceptance criteria met

### Per Epic
- [ ] All stories complete
- [ ] Integration tests pass
- [ ] No critical or high severity bugs
- [ ] Code reviewed

### Before Master Merge
- [ ] All epics complete
- [ ] Full regression test pass
- [ ] Performance benchmarks met
- [ ] UAT approved
- [ ] Documentation complete

---

## Tools & Setup

### JUnit Setup
If not already in project, add to `build.xml`:

```xml
<target name="test" depends="compile">
    <mkdir dir="build/test-classes"/>
    <javac srcdir="test" destdir="build/test-classes" 
           classpath="build/classes:lib/junit-4.12.jar" 
           source="1.8" target="1.8" debug="on" includeantruntime="false"/>
    
    <junit printsummary="yes" haltonfailure="no">
        <classpath>
            <pathelement location="build/classes"/>
            <pathelement location="build/test-classes"/>
            <pathelement location="lib/junit-4.12.jar"/>
        </classpath>
        
        <formatter type="plain" usefile="false"/>
        
        <batchtest>
            <fileset dir="test">
                <include name="**/*Test.java"/>
            </fileset>
        </batchtest>
    </junit>
</target>
```

### Test Logging
Enable debug logging for testing:

```java
// In test setup
CFG.LOG_LEVEL.set("DEBUG");
```

---

## Testing Schedule

### Weekly testing cadence:
- **Daily**: Run unit tests (automated)
- **End of each story**: Manual story tests
- **End of each epic**: Integration tests
- **Bi-weekly**: Performance benchmarks
- **Pre-release**: Full regression + UAT

### Test assignments:
- **Developer**: Unit tests, story-level manual tests
- **QA**: Epic integration tests, regression tests
- **Product owner**: UAT, usability testing

---

**Questions?** Contact the QA lead or refer to individual epic test cases.
