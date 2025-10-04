# Epic 5: Quantity Field Bot Implementation

**Epic ID**: FB-EPIC-5  
**Status**: Not Started  
**Priority**: P0 (Blocker)  
**Estimate**: 1.5 weeks  
**Dependencies**: Epic 1, Epic 4 (Farming Actions)  

## Epic Description
Implement the autonomous bot for quantity-focused fields that prioritizes throughput over quality. The bot will continuously cycle through tilling, planting, and harvesting with relaxed quality requirements (-4 to +5), managing inventory automatically and maximizing crop production.

## Goals
- Automate full farming cycle for quantity fields
- Implement field state machine (idle → tilling → planting → growing → harvesting)
- Manage seed inventory with quality preference
- Handle crop storage and sorting
- Optimize tile processing order for efficiency
- Handle error states and recovery
- Support continuous operation without user intervention

## User Stories

---

### Story FB-5.1: Design Quantity Field State Machine
**Story Points**: 5  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Design state machine diagram for quantity field lifecycle
- [ ] Define states:
  - INITIALIZING: Scanning field and determining work
  - TILLING: Processing untilled tiles
  - PLANTING: Planting seeds on tilled tiles
  - GROWING: Waiting for crops to mature
  - HARVESTING: Collecting mature crops
  - STORING: Depositing harvest to chests
  - RESTOCKING: Getting more seeds from chests
  - IDLE: No work needed, waiting for next cycle
  - ERROR: Unrecoverable error, needs user attention
- [ ] Define state transitions and conditions
- [ ] Document expected behavior in each state
- [ ] Create state machine implementation class

#### Technical Notes
```
INITIALIZING → TILLING (if untilled tiles exist)
           → PLANTING (if tilled tiles exist and seeds available)
           → HARVESTING (if harvestable crops exist)
           → GROWING (if all planted, nothing harvestable)
           → IDLE (if field complete)

TILLING → PLANTING (when all tiles tilled)
       → ERROR (if no hoe and tiles need tilling)

PLANTING → GROWING (when all tiles planted)
        → RESTOCKING (if no seeds)
        
HARVESTING → STORING (when inventory full or all harvested)
          → TILLING (if field cleared, start new cycle)

STORING → HARVESTING (if more crops to harvest)
       → TILLING (if done harvesting)
       
RESTOCKING → PLANTING (when seeds acquired)
          → ERROR (if no seeds in storage)

GROWING → HARVESTING (when crops harvestable)
       → GROWING (continue waiting)

ERROR → IDLE (when error resolved)
```

#### Files to Create
- `src/auto/farming/bot/QuantityFieldState.java` (enum)
- `feature/farming-bot/docs/QUANTITY-BOT-STATE-MACHINE.md`

---

### Story FB-5.2: Create Quantity Field Bot Core
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `QuantityFieldBot.java` class
- [ ] Implement bot lifecycle methods:
  - `start()`: Initialize and begin processing
  - `pause()`: Temporarily halt operations
  - `resume()`: Continue after pause
  - `stop()`: Shutdown gracefully
- [ ] Implement state machine execution loop
- [ ] Run bot on separate thread (not UI thread)
- [ ] Handle state transitions
- [ ] Emit status events for UI updates
- [ ] Add safety checks (player proximity, manual override)
- [ ] Implement tick-based execution (e.g., 1 tick per 500ms)

#### Technical Notes
```java
package auto.farming.bot;

public class QuantityFieldBot {
    private final FarmField field;
    private QuantityFieldState state;
    private volatile boolean running;
    private volatile boolean paused;
    
    public void start() {
        running = true;
        new Thread(this::run).start();
    }
    
    private void run() {
        while (running) {
            if (!paused) {
                tick();
            }
            Thread.sleep(500);
        }
    }
    
    private void tick() {
        switch (state) {
            case INITIALIZING: handleInitializing(); break;
            case TILLING: handleTilling(); break;
            // ...
        }
    }
}
```

#### Files to Create
- `src/auto/farming/bot/QuantityFieldBot.java`
- `src/auto/farming/bot/BotStatus.java` (data class for status reporting)

---

### Story FB-5.3: Implement Field Initialization
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `handleInitializing()` state handler
- [ ] Scan all tiles in field to determine current state
- [ ] Build work queues:
  - Tiles to till
  - Tiles to plant
  - Crops to harvest
- [ ] Estimate time to complete each phase
- [ ] Determine initial state based on field scan
- [ ] Cache tile states for performance
- [ ] Handle large fields efficiently (>500 tiles)
- [ ] Transition to appropriate next state

#### Technical Notes
- Use `TileDetector` from Epic 4 to scan field
- Batch tile scanning: process 50 tiles at a time to avoid lag
- Build priority queues: process tiles in optimal order
- Estimate time based on tile count and action durations

#### Files to Modify
- `src/auto/farming/bot/QuantityFieldBot.java`

#### Files to Create
- `src/auto/farming/bot/WorkQueue.java`

---

### Story FB-5.4: Implement Tilling Logic
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `handleTilling()` state handler
- [ ] Process tiles from tilling work queue
- [ ] Execute tilling actions using `FarmingActions.tillTile()`
- [ ] Update tile state after successful till
- [ ] Handle tilling failures (retry up to 3 times)
- [ ] Optimize tilling order (nearest-neighbor or row-by-row)
- [ ] Check for hoe availability before starting
- [ ] Transition to PLANTING when all tiles tilled
- [ ] Transition to ERROR if no hoe and unable to get one

#### Technical Notes
- Process one tile at a time, wait for completion
- Consider batching pathfinding for nearby tiles
- Track progress: tiles tilled / total tiles
- Emit progress events for UI

#### Files to Modify
- `src/auto/farming/bot/QuantityFieldBot.java`

---

### Story FB-5.5: Implement Seed Selection with Quality Preference
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `SeedInventoryManager.java` class
- [ ] Scan inventory for seeds matching field crop type
- [ ] Filter seeds by quality range (-4 to +5 for quantity fields)
- [ ] Sort seeds by quality (highest first, even for quantity)
- [ ] Select best available seed for planting
- [ ] Track seed count for restocking decisions
- [ ] Handle mixed-quality seed stacks
- [ ] Return null if no suitable seeds available

#### Technical Notes
- Quality preference: even quantity fields benefit from higher quality seeds
- Seed quality stored in item tooltip or GItem attributes
- May need to iterate inventory multiple times to find all seeds
- Cache inventory state to avoid repeated scans

#### Files to Create
- `src/auto/farming/bot/SeedInventoryManager.java`

---

### Story FB-5.6: Implement Planting Logic
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `handlePlanting()` state handler
- [ ] Process tiles from planting work queue
- [ ] Select best seed from inventory for each tile
- [ ] Execute planting actions using `FarmingActions.plantSeed()`
- [ ] Track planted seed quality per tile
- [ ] Update tile state after successful plant
- [ ] Handle planting failures (retry up to 3 times)
- [ ] Transition to GROWING when all tiles planted
- [ ] Transition to RESTOCKING if seeds depleted

#### Technical Notes
- Use `SeedInventoryManager` to select seeds
- Record planted quality for future quality tracking
- Emit planting events with quality info for statistics

#### Files to Modify
- `src/auto/farming/bot/QuantityFieldBot.java`

---

### Story FB-5.7: Implement Growing State Monitoring
**Story Points**: 5  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `handleGrowing()` state handler
- [ ] Periodically scan field for harvestable crops (every 60 seconds)
- [ ] Detect when crops reach harvestable state
- [ ] Transition to HARVESTING when any crops ready
- [ ] Add configurable scan interval
- [ ] Show estimated time until harvest (if determinable)
- [ ] Handle crops maturing at different rates

#### Technical Notes
- Growing state is mostly idle, just periodic checks
- Use `TileDetector` to check growth stages
- May be able to estimate harvest time from growth stages
- Don't scan too frequently to avoid performance impact

#### Files to Modify
- `src/auto/farming/bot/QuantityFieldBot.java`

---

### Story FB-5.8: Implement Harvesting Logic
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `handleHarvesting()` state handler
- [ ] Process crops from harvest work queue
- [ ] Execute harvesting actions using `FarmingActions.harvestCrop()`
- [ ] Track harvested crop quality and quantity
- [ ] Update tile state after successful harvest
- [ ] Handle harvesting failures (retry up to 3 times)
- [ ] Monitor inventory space, transition to STORING when full
- [ ] Transition to TILLING when all crops harvested
- [ ] Record harvest statistics (total yield, avg quality)

#### Technical Notes
- Inventory may fill before all crops harvested
- Prioritize harvestable crops (don't harvest unripe)
- Track harvest yield for statistics and notifications

#### Files to Modify
- `src/auto/farming/bot/QuantityFieldBot.java`

#### Files to Create
- `src/auto/farming/bot/HarvestStats.java`

---

### Story FB-5.9: Implement Storage Management
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `handleStoring()` state handler
- [ ] Find nearest output chest linked to field
- [ ] Navigate to chest
- [ ] Open chest inventory
- [ ] Transfer harvested crops from inventory to chest
- [ ] Optionally sort by quality (high quality to chest A, low to chest B)
- [ ] Handle chest full scenario (try next chest or alert user)
- [ ] Return to field after storing
- [ ] Transition back to HARVESTING if more crops remain
- [ ] Transition to TILLING if harvest complete

#### Technical Notes
- Use chest links from `StorageConfig` (Epic 3)
- Chest interaction may require specific UI interactions
- Handle multiple output chests for quality sorting
- Ensure pathfinding back to field works

#### Files to Create
- `src/auto/farming/bot/StorageManager.java`

---

### Story FB-5.10: Implement Seed Restocking
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `handleRestocking()` state handler
- [ ] Find nearest input chest linked to field
- [ ] Navigate to chest
- [ ] Open chest inventory
- [ ] Withdraw seeds matching field crop type
- [ ] Prefer higher quality seeds (within -4 to +5 range)
- [ ] Take enough seeds to plant remaining tiles
- [ ] Handle no seeds available (transition to ERROR, notify user)
- [ ] Return to field after restocking
- [ ] Transition to PLANTING when seeds acquired

#### Technical Notes
- Calculate needed seed count: unplanted tiles - inventory seeds
- Don't overfill inventory, leave space for harvest later
- Alert user if no seeds in any input chest

#### Files to Modify
- `src/auto/farming/bot/StorageManager.java`

---

### Story FB-5.11: Implement Error Handling and Recovery
**Story Points**: 13  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Implement `handleError()` state handler
- [ ] Define error types:
  - NO_SEEDS: No seeds available in storage
  - NO_HOE: No hoe available for tilling
  - PATHFINDING_FAILED: Cannot reach field/chest
  - INVENTORY_FULL: No space to harvest
  - CHEST_FULL: Cannot store crops
  - UNKNOWN: Unexpected error
- [ ] Emit error notifications to user
- [ ] Attempt automatic recovery where possible
- [ ] Transition to IDLE after error resolution
- [ ] Log errors for debugging
- [ ] Add manual error reset action

#### Technical Notes
- Some errors recoverable (get hoe from storage)
- Some require user intervention (add seeds to chest)
- Error notifications should be prominent but not spammy

#### Files to Create
- `src/auto/farming/bot/BotError.java` (class with error details)
- `src/auto/farming/bot/ErrorRecovery.java`

---

### Story FB-5.12: Add Bot Statistics Tracking
**Story Points**: 5  
**Priority**: P2  

#### Acceptance Criteria
- [ ] Track statistics per field:
  - Total cycles completed
  - Total crops harvested
  - Average crop quality
  - Highest/lowest quality harvested
  - Total runtime
  - Success rate (actions succeeded / attempted)
- [ ] Persist statistics to config file
- [ ] Provide API to retrieve stats for UI display
- [ ] Reset statistics on user request
- [ ] Export statistics to CSV/JSON

#### Technical Notes
- Statistics useful for monitoring bot effectiveness
- Display in UI (Epic 3 integration)
- Track per-field and global aggregates

#### Files to Create
- `src/auto/farming/bot/BotStatistics.java`

---

### Story FB-5.13: Create Quantity Bot Integration Tests
**Story Points**: 13  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Test complete farming cycle on test field
- [ ] Test state transitions
- [ ] Test error scenarios (no seeds, no hoe)
- [ ] Test inventory management (storing, restocking)
- [ ] Test pause/resume functionality
- [ ] Test with various field sizes (small, medium, large)
- [ ] Validate statistics accuracy
- [ ] Document test scenarios and results

#### Technical Notes
- Requires live game environment for full integration test
- Create test fields of different sizes
- Run bot for multiple cycles to validate stability

#### Files to Create
- `test/auto/farming/bot/QuantityFieldBotTest.java`
- `feature/farming-bot/docs/QUANTITY-BOT-TESTING.md`

---

## Definition of Done
- [ ] All stories completed and code reviewed
- [ ] Quantity bot can autonomously farm a field through multiple cycles
- [ ] Inventory management (seeds, harvest) works correctly
- [ ] State machine handles all transitions properly
- [ ] Error handling and recovery functional
- [ ] Statistics tracking accurate
- [ ] Code follows project style guidelines
- [ ] Integration tests passing
- [ ] Bot stable for 24-hour continuous operation

## Dependencies
- **Epic 1**: Field data structures
- **Epic 4**: Farming actions (till, plant, harvest)
- **Epic 3**: Storage configuration (chest links)

## Blocks
- **Epic 7**: Multi-field scheduler depends on single-field bot working

## Technical Debt
- Pathfinding optimization for large fields
- Advanced seed selection strategies (future: predict optimal quality distribution)
- Adaptive timing based on network latency
- Better handling of unexpected interruptions (player movement, server lag)

## Risks
- Bot may get stuck in certain states (mitigation: timeout and error detection)
- Inventory management may fail with complex chest setups (mitigation: extensive testing)
- Performance issues on very large fields (mitigation: profiling and optimization)

## Testing Strategy
1. Unit tests for state machine logic
2. Integration tests for each state handler
3. End-to-end tests for full farming cycles
4. Stress tests with large fields and multiple cycles
5. Error injection tests (remove tools, fill inventory, etc.)
6. Long-running stability tests (24+ hours)
