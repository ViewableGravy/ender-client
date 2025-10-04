# Epic 4: Tile Detection & Basic Farming Actions

**Epic ID**: FB-EPIC-4  
**Status**: Not Started  
**Priority**: P0 (Blocker)  
**Estimate**: 2 weeks  
**Dependencies**: Epic 1 (Field Data Structures)  

## Epic Description
Implement core functionality for detecting tile states, identifying crops, reading quality values, and executing basic farming actions (tilling, planting, harvesting). This epic establishes the fundamental interaction between the bot and the game world.

## Goals
- Detect tile states (untilled, tilled, planted, harvestable)
- Identify crop types and growth stages from Gobs
- Read crop quality values
- Execute tilling/hoeing actions
- Execute planting actions
- Execute harvesting actions
- Integrate with pathfinding
- Handle tool selection and switching

## User Stories

---

### Story FB-4.1: Research Haven Farming Mechanics
**Story Points**: 8  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Research and document how farming works in Haven & Hearth
- [ ] Identify relevant Gob types for crops (resource names)
- [ ] Understand tile states and how they're represented in code
- [ ] Document crop growth stages and progression
- [ ] Research quality system and how quality is stored/displayed
- [ ] Understand watering mechanics (if applicable)
- [ ] Document tool requirements (hoe, seeds)
- [ ] Identify relevant network messages for farming actions
- [ ] Create technical reference document

#### Research Areas
- Check Ring of Brodgar wiki: https://ringofbrodgar.com/
- Examine existing crop-related code in `src/haven/`
- Study `Gob.java` for crop attributes
- Review `GAttrib.java` implementations for crop data
- Check `Resource.java` for crop resource patterns
- Examine inventory item interactions

#### Files to Create
- `feature/farming-bot/docs/FARMING-MECHANICS-RESEARCH.md`

---

### Story FB-4.2: Create Crop Resource Name Registry
**Story Points**: 5  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `CropResources.java` class with crop resource name constants
- [ ] Document resource names for common crops:
  - Wheat
  - Carrots
  - Turnips
  - Barley
  - Hemp
  - Flax
  - Pipeweed
  - (Add more as discovered)
- [ ] Add regex patterns for crop detection (similar to `ResName.java`)
- [ ] Include growth stage suffixes/patterns
- [ ] Add utility methods: `isCrop(String resName)`, `getCropType(String resName)`
- [ ] Document crop resource naming conventions

#### Technical Notes
```java
package auto.farming.game;

public class CropResources {
    public static final String WHEAT = "gfx/terobjs/plants/wheat";
    public static final String CARROT = "gfx/terobjs/plants/carrot";
    // ...
    
    public static final Pattern CROP_PATTERN = Pattern.compile("gfx/terobjs/plants/.*");
    
    public static boolean isCrop(String resName) {
        return CROP_PATTERN.matcher(resName).matches();
    }
}
```

#### Files to Create
- `src/auto/farming/game/CropResources.java`

---

### Story FB-4.3: Implement Tile State Detection
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `TileDetector.java` class for analyzing tile states
- [ ] Implement `getTileState(Coord2d worldCoord)` method
- [ ] Detect untilled land (check tile type/overlay)
- [ ] Detect tilled land (plowed/ready for planting)
- [ ] Detect planted crops (Gob present on tile)
- [ ] Identify crop growth stage (young, growing, harvestable)
- [ ] Handle edge cases: multiple Gobs on same tile, partial tile overlap
- [ ] Add caching to avoid repeated checks
- [ ] Support batch tile scanning for field analysis

#### Technical Notes
- Use `MapView.glob.oc` to query Gobs at coordinates
- Tilled state may be a tile overlay or specific tile type
- Growth stages may be encoded in Gob resource name or attributes
- Consider performance: scanning 500 tiles should take <100ms

#### Files to Create
- `src/auto/farming/game/TileDetector.java`
- `src/auto/farming/game/TileInfo.java` (data class for tile info)

---

### Story FB-4.4: Implement Crop Quality Detection
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `CropInspector.java` class for crop analysis
- [ ] Implement `getCropQuality(Gob cropGob)` method
- [ ] Read quality from Gob attributes (GAttrib)
- [ ] Handle different quality representation formats (absolute, relative)
- [ ] Support quality inspection without needing to harvest
- [ ] Add fallback methods if direct quality reading unavailable
- [ ] Cache quality values to minimize Gob inspection overhead
- [ ] Handle crops without quality (e.g., wild plants)

#### Technical Notes
- Quality may be in `GobQuality` or similar GAttrib
- May need to interact with crop (right-click inspect) to reveal quality
- Study existing quality display code in `GobInfo` classes
- Quality format: likely integer (0-100) or fractional (0.0-1.0)

#### Files to Create
- `src/auto/farming/game/CropInspector.java`
- `src/auto/farming/game/CropQuality.java` (data class)

---

### Story FB-4.5: Implement Tilling Action
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `FarmingActions.java` class for farming operations
- [ ] Implement `tillTile(Coord2d worldCoord)` method
- [ ] Select hoe from inventory automatically
- [ ] Execute till action at target coordinates
- [ ] Wait for action completion
- [ ] Verify tile state changed to tilled
- [ ] Handle failures (no hoe, tile unreachable, action interrupted)
- [ ] Return action result (SUCCESS, FAILED, NO_TOOL, etc.)
- [ ] Add action timeout (e.g., 10 seconds)

#### Technical Notes
- Tilling uses flower menu action or direct inventory item use
- May need to pathfind to tile first (see next story)
- Action completion detected via tile state change or animation end
- Reference existing action patterns in `auto/Actions.java`

#### Files to Create
- `src/auto/farming/game/FarmingActions.java`
- `src/auto/farming/game/ActionResult.java` (enum)

---

### Story FB-4.6: Integrate Pathfinding for Farming Actions
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `FarmingPathfinder.java` wrapper around existing pathfinding
- [ ] Implement `pathToTile(Coord2d target)` method
- [ ] Calculate optimal position for interacting with tile (adjacent)
- [ ] Handle pathfinding failures (unreachable, blocked)
- [ ] Add movement validation (reached target position)
- [ ] Implement wait-for-arrival logic with timeout
- [ ] Support queuing multiple tile visits for efficiency
- [ ] Add obstacle avoidance and retry logic

#### Technical Notes
- Leverage existing pathfinding from `auto/Actions.java` or game's pathfinding
- Character must be adjacent to tile to interact
- Optimize path for visiting multiple tiles in sequence (traveling salesman)
- Handle dynamic obstacles (other players, animals)

#### Files to Create
- `src/auto/farming/game/FarmingPathfinder.java`

---

### Story FB-4.7: Implement Planting Action
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `plantSeed(Coord2d worldCoord, String cropType)` method
- [ ] Find seed in inventory matching crop type
- [ ] Select seed from inventory
- [ ] Execute plant action at target tile
- [ ] Wait for planting animation/completion
- [ ] Verify crop Gob spawned on tile
- [ ] Handle failures (no seeds, tile not tilled, wrong seed type)
- [ ] Support planting specific quality seeds (select highest quality available)
- [ ] Return planted seed quality for tracking

#### Technical Notes
- Seeds identified by resource name (e.g., "Wheat Seed")
- Planting may use right-click on tile with seed selected
- Seed quality stored in item tooltip or GItem attributes
- Priority: plant highest quality seeds first for quality fields

#### Files to Modify
- `src/auto/farming/game/FarmingActions.java`

#### Files to Create
- `src/auto/farming/game/SeedSelector.java`

---

### Story FB-4.8: Implement Harvesting Action
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `harvestCrop(Coord2d worldCoord)` method
- [ ] Detect harvestable state (crop fully grown)
- [ ] Execute harvest action (right-click or flower menu)
- [ ] Wait for harvest animation completion
- [ ] Verify crop removed from tile (Gob despawned)
- [ ] Detect harvested items in inventory
- [ ] Read harvested crop quality
- [ ] Handle failures (not harvestable, inventory full, action interrupted)
- [ ] Return harvest result with quality and quantity

#### Technical Notes
- Harvestable state may be specific growth stage or Gob attribute
- Harvested items appear in inventory (detect via inventory change event)
- Quality may need to be read before harvesting (pre-inspect)
- Handle inventory full: stop harvesting and notify user

#### Files to Modify
- `src/auto/farming/game/FarmingActions.java`

#### Files to Create
- `src/auto/farming/game/HarvestResult.java` (data class)

---

### Story FB-4.9: Implement Tool Management
**Story Points**: 13  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Create `ToolManager.java` class for tool handling
- [ ] Detect available tools in inventory (hoe, seeds)
- [ ] Implement tool selection logic
- [ ] Auto-equip correct tool for action
- [ ] Monitor tool durability/wear
- [ ] Alert when tool broken or missing
- [ ] Support multiple tool types (different quality hoes)
- [ ] Return to storage to get tools if missing

#### Technical Notes
- Tools identified by resource name patterns
- Durability may be in item attributes (check GItem.meter)
- Some actions may not require tool equipped, just in inventory
- Consider tool switching overhead in action sequencing

#### Files to Create
- `src/auto/farming/game/ToolManager.java`
- `src/auto/farming/game/ToolType.java` (enum)

---

### Story FB-4.10: Create Action Sequence Builder
**Story Points**: 13  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Create `ActionSequence.java` class for chaining actions
- [ ] Support building sequences: pathfind → till → plant
- [ ] Implement error handling and rollback
- [ ] Add logging for action execution
- [ ] Support pause/resume mid-sequence
- [ ] Calculate estimated time for sequence
- [ ] Implement retry logic for failed actions (max 3 retries)
- [ ] Emit events for action completion (for UI updates)

#### Technical Notes
```java
ActionSequence seq = new ActionSequence()
    .pathTo(tile)
    .till()
    .selectSeed("wheat")
    .plant()
    .onComplete(() -> updateTileState())
    .onError(err -> logError(err))
    .execute();
```

#### Files to Create
- `src/auto/farming/game/ActionSequence.java`
- `src/auto/farming/game/ActionLogger.java`

---

### Story FB-4.11: Add Watering Support (If Applicable)
**Story Points**: 8  
**Priority**: P2  

#### Acceptance Criteria
- [ ] Research if watering exists in Haven farming
- [ ] If yes, implement `waterCrop(Coord2d worldCoord)` method
- [ ] Detect crops that need watering
- [ ] Find water source (barrel, well, pond)
- [ ] Fill watering can/bucket
- [ ] Execute water action on crops
- [ ] Track watering status per tile
- [ ] Skip if watering not needed for crop type

#### Technical Notes
- This story may be N/A if watering isn't part of Haven farming
- If watering exists, it's likely optional or crop-specific
- Defer implementation if research shows it's not needed

#### Files to Modify
- `src/auto/farming/game/FarmingActions.java` (if applicable)

---

### Story FB-4.12: Create Farming Actions Test Suite
**Story Points**: 21  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Create integration tests for tile detection
- [ ] Test tilling action on various tile types
- [ ] Test planting with different seed types/qualities
- [ ] Test harvesting at different growth stages
- [ ] Test pathfinding to tiles
- [ ] Test error handling (no tools, inventory full)
- [ ] Test action sequencing
- [ ] Create test scenarios document

#### Technical Notes
- Tests may require live game environment or mocking
- Create test fields in controlled area
- Document expected behavior for each action
- Automated testing challenging for game interactions

#### Files to Create
- `test/auto/farming/game/TileDetectorTest.java`
- `test/auto/farming/game/FarmingActionsTest.java`
- `feature/farming-bot/docs/ACTIONS-TESTING-GUIDE.md`

---

## Definition of Done
- [ ] All stories completed and code reviewed
- [ ] Bot can reliably detect tile states
- [ ] Bot can successfully till, plant, and harvest tiles
- [ ] Pathfinding works for navigating to tiles
- [ ] Tool management functional
- [ ] Actions handle errors gracefully
- [ ] Code follows project style guidelines
- [ ] Integration tests validate core actions
- [ ] Documentation complete for all mechanics

## Dependencies
- **Epic 1**: Field data structures (TileState model)
- Haven's Gob system for crop detection
- Existing pathfinding implementation
- Inventory system for tool/seed management

## Blocks
- **Epic 5 & 6**: Bot logic depends on these actions working
- **Epic 2**: Overlay can show tile states detected by this epic

## Technical Debt
- Quality detection may need refinement based on game mechanics
- Pathfinding optimization for large fields
- Action timing may need calibration (wait durations)
- Error recovery strategies may need enhancement

## Risks
- Game mechanics may differ from assumptions (mitigation: early prototyping)
- Quality values may not be accessible (mitigation: research alternative methods)
- Network latency may affect action reliability (mitigation: add retries and timeouts)

## Testing Strategy
1. Unit tests for crop resource detection
2. Integration tests with live game environment
3. Manual testing for each action type
4. Performance testing for batch tile scanning
5. Error scenario testing (missing tools, full inventory, etc.)
