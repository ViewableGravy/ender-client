# Epic 1: Field Data Structures & Core Models

**Epic ID**: FB-EPIC-1  
**Status**: Not Started  
**Priority**: P0 (Blocker)  
**Estimate**: 1 week  
**Dependencies**: None  

## Epic Description
Implement the foundational data structures and models for representing farm fields, crop configurations, and field states. This epic establishes the core domain model that all other components will build upon.

## Goals
- Define field representation with grid-based coordinate system
- Support multiple field shapes (rectangles, circles)
- Enable field subdivision (quadrants, segments)
- Create crop configuration model
- Implement field state tracking
- Build data serialization/deserialization

## User Stories

---

### Story FB-1.1: Create Field Data Model
**Story Points**: 8  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `FarmField.java` class in `src/auto/farming/model/`
- [ ] Implement properties:
  - Unique field ID (UUID)
  - Field name (user-defined string)
  - Field type enum (QUALITY, QUANTITY)
  - Shape type enum (RECTANGLE, CIRCLE)
  - Bounding coordinates (grid-based)
  - Crop type (resource name)
  - Enabled/disabled flag
  - Creation timestamp
- [ ] Implement `equals()`, `hashCode()`, `toString()` methods
- [ ] Add validation for required fields
- [ ] Include Javadoc documentation

#### Technical Notes
```java
package auto.farming.model;

public class FarmField {
    private final UUID id;
    private String name;
    private FieldType type;
    private FieldShape shape;
    private Coord2d gridOrigin;
    private String cropType; // Resource name e.g., "gfx/kritter/horse/stallion"
    private boolean enabled;
    private long createdAt;
    // ... getters, setters, validation
}
```

#### Files to Create
- `src/auto/farming/model/FarmField.java`

---

### Story FB-1.2: Implement Field Type Enums
**Story Points**: 2  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `FieldType.java` enum with values: QUALITY, QUANTITY
- [ ] Create `FieldShape.java` enum with values: RECTANGLE, CIRCLE
- [ ] Create `FieldState.java` enum with values: IDLE, TILLING, PLANTING, GROWING, HARVESTABLE, HARVESTING, ERROR
- [ ] Add display names and descriptions for UI rendering
- [ ] Include color codes for overlay rendering

#### Technical Notes
```java
public enum FieldType {
    QUALITY("Quality Breeding", "Focus on increasing crop quality"),
    QUANTITY("Quantity Production", "Mass production with relaxed quality");
    
    private final String displayName;
    private final String description;
    // ... constructor, getters
}
```

#### Files to Create
- `src/auto/farming/model/FieldType.java`
- `src/auto/farming/model/FieldShape.java`
- `src/auto/farming/model/FieldState.java`

---

### Story FB-1.3: Create Grid-Based Coordinate System
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `FieldGrid.java` class to represent field boundaries
- [ ] Support rectangular grid definition (min/max x/y coordinates)
- [ ] Support circular grid definition (center point + radius)
- [ ] Implement `contains(Coord2d point)` method for boundary checking
- [ ] Implement `getTiles()` method to return all grid tiles in field
- [ ] Support grid subdivision for circles (quadrants, fifths, etc.)
- [ ] Add utility methods: `getCenter()`, `getArea()`, `getBounds()`
- [ ] Ensure grid aligns with Haven's tile system (11x11 unit tiles)

#### Technical Notes
- Haven uses 11x11 unit tiles for the grid system
- Need to convert between world coordinates and grid coordinates
- Circle subdivision should create equal-area segments when possible
- Reference existing grid systems in `MapView.java` and `MCache.java`

#### Files to Create
- `src/auto/farming/model/FieldGrid.java`
- `src/auto/farming/util/GridUtils.java` (helper methods)

---

### Story FB-1.4: Implement Field Subdivision System
**Story Points**: 13  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Create `FieldSubdivision.java` interface
- [ ] Implement `RectangleSubdivision.java` (rows/columns)
- [ ] Implement `CircleSubdivision.java` (radial segments)
- [ ] Support configurable subdivision counts (2, 4, 5, 8, etc.)
- [ ] Each subdivision returns a `FieldGrid` representing its area
- [ ] Add `getSubdivision(int index)` method
- [ ] Add `getAllSubdivisions()` method
- [ ] Ensure no tile overlap between subdivisions
- [ ] Handle edge cases (very small fields, odd counts)

#### Technical Notes
- Subdivisions allow different crops in different parts of same field
- Circle subdivisions should use "pie slice" radial segments
- Each subdivision acts as an independent mini-field
- May need to store subdivision metadata in `FarmField`

#### Files to Create
- `src/auto/farming/model/subdivision/FieldSubdivision.java` (interface)
- `src/auto/farming/model/subdivision/RectangleSubdivision.java`
- `src/auto/farming/model/subdivision/CircleSubdivision.java`
- `src/auto/farming/model/subdivision/SubdivisionConfig.java`

---

### Story FB-1.5: Create Crop Configuration Model
**Story Points**: 8  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `CropConfig.java` class
- [ ] Implement properties:
  - Crop resource name (e.g., "gfx/terobjs/plants/carrot")
  - Display name
  - Growth stages (number of stages)
  - Watering required (boolean)
  - Min quality threshold (for quantity fields: -4)
  - Max quality threshold (for quality fields: +5)
  - Harvest yield estimate
- [ ] Add validation for quality thresholds
- [ ] Support default configs for common crops
- [ ] Implement builder pattern for easy configuration

#### Technical Notes
- Research existing crop resource names in game
- May need to discover crop properties dynamically from `Resource.java`
- Quality thresholds: Quality fields use 0 to +5, Quantity fields use -4 to +5

#### Files to Create
- `src/auto/farming/model/CropConfig.java`
- `src/auto/farming/config/DefaultCropConfigs.java`

---

### Story FB-1.6: Implement Tile State Tracking
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `TileState.java` class to track individual tile status
- [ ] Implement properties:
  - Grid coordinates (Coord2d)
  - Current state (UNTILLED, TILLED, PLANTED, GROWING_X, HARVESTABLE)
  - Planted crop type (if any)
  - Planted crop quality (if known)
  - Last updated timestamp
  - Growth stage (0-N)
- [ ] Create `FieldTileMap.java` to manage all tiles in a field
- [ ] Implement `updateTile(Coord2d, TileState)` method
- [ ] Implement `getTile(Coord2d)` method
- [ ] Add `getTilesByState(TileStateEnum)` query method
- [ ] Support bulk state updates for efficiency

#### Technical Notes
- Need to efficiently track potentially hundreds of tiles per field
- Use HashMap for O(1) lookups by coordinate
- May need to periodically scan field to update tile states
- Growth stages may need to be inferred from Gob inspection

#### Files to Create
- `src/auto/farming/model/TileState.java`
- `src/auto/farming/model/FieldTileMap.java`
- `src/auto/farming/model/TileStateEnum.java`

---

### Story FB-1.7: Create Storage Configuration Model
**Story Points**: 8  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Create `StorageConfig.java` class
- [ ] Support multiple input chests (seed sources)
- [ ] Support multiple output chests (harvest destinations)
- [ ] Link chests to fields via field ID
- [ ] Store chest coordinates and types
- [ ] Support quality-based output sorting rules
- [ ] Add validation for chest accessibility (distance checks)
- [ ] Implement `findNearestInputChest()` and `findNearestOutputChest()` methods

#### Technical Notes
- Chests identified by world coordinates (Coord2d)
- May need to detect chest type from Gob (chest vs trough)
- Quality sorting: high quality to chest A, low quality to chest B
- Distance checks: ensure chest is within reasonable pathfinding range

#### Files to Create
- `src/auto/farming/model/StorageConfig.java`
- `src/auto/farming/model/ChestLink.java`

---

### Story FB-1.8: Implement Data Serialization
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create JSON schema for farming configuration
- [ ] Implement Gson serializers/deserializers for all models
- [ ] Create `FarmingConfig.java` root configuration class
- [ ] Support saving to `etc/farming.json5`
- [ ] Support loading from `etc/farming.json5`
- [ ] Handle versioning for future schema changes
- [ ] Add migration support for config updates
- [ ] Validate loaded data and provide error messages
- [ ] Create default empty configuration if file missing

#### Technical Notes
- Use existing Gson instance from client (see `Config.java` patterns)
- JSON5 format allows comments for user-friendly editing
- Store in user home directory: `~/.haven/farming.json5`
- Include schema version number for future migrations

#### Example JSON Structure
```json5
{
  "version": 1,
  "fields": [
    {
      "id": "uuid-here",
      "name": "North Wheat Field",
      "type": "QUANTITY",
      "shape": "RECTANGLE",
      "grid": {
        "minX": 100,
        "minY": 200,
        "maxX": 120,
        "maxY": 220
      },
      "cropType": "gfx/terobjs/plants/wheat",
      "enabled": true
    }
  ],
  "storage": [ /* ... */ ]
}
```

#### Files to Create
- `src/auto/farming/config/FarmingConfig.java`
- `src/auto/farming/config/ConfigSerializer.java`
- `src/auto/farming/config/ConfigLoader.java`
- `etc/farming-schema.json5` (example/template)

---

### Story FB-1.9: Create Field Manager Singleton
**Story Points**: 8  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `FarmingManager.java` singleton class
- [ ] Implement field CRUD operations:
  - `addField(FarmField field)`
  - `updateField(FarmField field)`
  - `removeField(UUID id)`
  - `getField(UUID id)`
  - `getAllFields()`
- [ ] Maintain in-memory field registry
- [ ] Trigger config save on field changes
- [ ] Provide observable/listener pattern for UI updates
- [ ] Implement `initialize()` method to load config on startup
- [ ] Add `shutdown()` method to save config on exit

#### Technical Notes
- Singleton ensures single source of truth for field data
- Use RxJava `Reactor` pattern for change notifications (see existing patterns)
- Hook into client lifecycle for init/shutdown (see `MainFrame.java`)
- Thread-safe for concurrent access from UI and bot threads

#### Files to Create
- `src/auto/farming/FarmingManager.java`

---

### Story FB-1.10: Add Unit Tests for Models
**Story Points**: 13  
**Priority**: P2  

#### Acceptance Criteria
- [ ] Create JUnit tests for `FarmField` validation
- [ ] Create tests for `FieldGrid` boundary detection
- [ ] Create tests for subdivision calculations
- [ ] Create tests for tile state tracking
- [ ] Create tests for JSON serialization/deserialization
- [ ] Test edge cases: empty fields, invalid coordinates, null values
- [ ] Achieve >80% code coverage for model classes
- [ ] Add test data fixtures for common scenarios

#### Technical Notes
- May need to set up JUnit in build.xml if not already present
- Create test resources in `test/resources/`
- Mock Haven-specific dependencies (Coord2d, etc.)

#### Files to Create
- `test/auto/farming/model/FarmFieldTest.java`
- `test/auto/farming/model/FieldGridTest.java`
- `test/auto/farming/model/FieldSubdivisionTest.java`
- `test/auto/farming/config/ConfigSerializationTest.java`
- `test/resources/test-farming-config.json5`

---

## Definition of Done
- [ ] All stories completed and code reviewed
- [ ] Unit tests passing with >80% coverage
- [ ] JSON serialization working for all models
- [ ] Field data can be created, saved, loaded, and modified
- [ ] Code follows project style guidelines (4-space indent, LF line endings)
- [ ] Javadoc documentation complete for public APIs
- [ ] No compiler warnings or errors
- [ ] Integration tested with existing client startup/shutdown

## Dependencies
- None (foundational epic)

## Blocks
- Epic 2 (UI) depends on completion of this epic
- Epic 4 (Tile Operations) depends on FieldGrid and TileState models

## Technical Debt
- May need to refactor coordinate system if Haven's grid differs from assumptions
- Quality detection mechanism TBD (depends on game research in Epic 4)
- Performance optimization may be needed for large fields (>1000 tiles)
