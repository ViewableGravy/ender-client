# Epic 7: Multi-Field Orchestration & Scheduler

**Epic ID**: FB-EPIC-7  
**Status**: Not Started  
**Priority**: P1 (High)  
**Estimate**: 1.5 weeks  
**Dependencies**: Epic 5 (Quantity Bot), Epic 6 (Quality Bot)  

## Epic Description
Implement the orchestration layer that manages multiple farming fields simultaneously, prioritizing work across fields, coordinating resource access, optimizing travel between fields, and ensuring efficient bot operation across the entire farm. This epic transforms individual field bots into a cohesive multi-field farming system.

## Goals
- Schedule work across multiple active fields
- Prioritize fields based on state and urgency
- Optimize pathfinding between fields
- Coordinate shared resource access (chests, tools)
- Balance workload across fields
- Handle global pause/resume/stop
- Minimize idle time and maximize throughput
- Provide system-wide status and statistics

## User Stories

---

### Story FB-7.1: Design Multi-Field Scheduling Architecture
**Story Points**: 8  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Design architecture for multi-field coordination
- [ ] Define scheduling algorithm and priorities
- [ ] Document field state priorities:
  - CRITICAL: Crops harvestable and about to spoil (highest priority)
  - HIGH: Harvestable crops ready for collection
  - MEDIUM: Tilled tiles ready for planting with seeds available
  - LOW: Growing fields (periodic check only)
  - IDLE: No work needed
- [ ] Design resource locking mechanism (prevent concurrent chest access)
- [ ] Create sequence diagram for multi-field workflow
- [ ] Define performance targets (handle 10+ fields efficiently)

#### Technical Notes
- Priority system ensures urgent work (harvesting) happens first
- Resource locks prevent race conditions (two bots accessing same chest)
- Consider travel time in scheduling: prioritize nearby fields
- Balance: high-priority urgent work vs efficient batching

#### Files to Create
- `feature/farming-bot/docs/MULTI-FIELD-SCHEDULING-DESIGN.md`

---

### Story FB-7.2: Create Farming Scheduler Core
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `FarmingScheduler.java` class
- [ ] Implement scheduler lifecycle:
  - `start()`: Initialize and start all enabled field bots
  - `pause()`: Pause all field bots
  - `resume()`: Resume all paused bots
  - `stop()`: Stop all bots gracefully
- [ ] Maintain registry of all field bots (quantity and quality)
- [ ] Run scheduler on dedicated thread
- [ ] Implement scheduling tick (e.g., every 5 seconds)
- [ ] Coordinate field bot execution
- [ ] Handle scheduler errors and recovery
- [ ] Emit system-wide status events

#### Technical Notes
```java
package auto.farming;

public class FarmingScheduler {
    private final Map<UUID, FieldBot> fieldBots = new HashMap<>();
    private final PriorityQueue<FieldWorkItem> workQueue;
    private volatile boolean running;
    
    public void start() {
        initializeFieldBots();
        running = true;
        new Thread(this::schedulerLoop).start();
    }
    
    private void schedulerLoop() {
        while (running) {
            updateWorkQueue();
            scheduleNextWork();
            Thread.sleep(5000);
        }
    }
}
```

#### Files to Create
- `src/auto/farming/FarmingScheduler.java`
- `src/auto/farming/FieldBot.java` (interface/base class for quantity & quality bots)

---

### Story FB-7.3: Implement Field Priority System
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `FieldPriority.java` enum (CRITICAL, HIGH, MEDIUM, LOW, IDLE)
- [ ] Implement priority calculation for each field:
  - Calculate based on field state
  - Consider time-sensitive factors (crops ready to harvest)
  - Factor in resource availability (seeds, storage space)
  - Account for travel distance from current position
- [ ] Create priority queue for work items
- [ ] Re-evaluate priorities each scheduler tick
- [ ] Support manual priority overrides (pin field to top)
- [ ] Log priority changes for debugging

#### Technical Notes
- Priority calculation example:
  ```
  if (harvestable_crops > 0 && time_since_harvestable > threshold):
      priority = CRITICAL
  elif (harvestable_crops > 0):
      priority = HIGH
  elif (tilled_tiles > 0 && seeds_available):
      priority = MEDIUM
  elif (growing_crops > 0):
      priority = LOW
  else:
      priority = IDLE
  ```

#### Files to Create
- `src/auto/farming/scheduler/FieldPriority.java` (enum)
- `src/auto/farming/scheduler/PriorityCalculator.java`
- `src/auto/farming/scheduler/FieldWorkItem.java` (work queue item)

---

### Story FB-7.4: Implement Work Queue Management
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create work queue with priority ordering
- [ ] Implement `enqueueField(FarmField field, FieldPriority priority)`
- [ ] Implement `dequeueNextWork()` to get highest priority field
- [ ] Support field filtering (enabled/disabled)
- [ ] Handle queue updates when field states change
- [ ] Implement work item expiration (re-evaluate stale items)
- [ ] Add queue inspection methods for debugging
- [ ] Thread-safe queue operations

#### Technical Notes
- Use `PriorityQueue<FieldWorkItem>` with custom comparator
- Work items: { field, priority, timestamp, estimated_duration }
- Re-queue fields after work completion for continuous operation
- Consider secondary sorting by travel distance for same priority

#### Files to Create
- `src/auto/farming/scheduler/WorkQueue.java`

---

### Story FB-7.5: Implement Resource Locking System
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `ResourceLockManager.java` for coordinating shared resources
- [ ] Implement locking for:
  - Chests (by coordinate)
  - Tools (by type)
  - Character (only one bot action at a time)
- [ ] Support lock acquisition with timeout
- [ ] Implement lock release on bot completion/error
- [ ] Detect and resolve deadlocks
- [ ] Add lock debugging/monitoring
- [ ] Thread-safe lock operations

#### Technical Notes
- Prevent two bots from accessing same chest simultaneously
- Prevent one bot from starting while another using character
- Use `ReentrantLock` or similar for implementation
- Timeout prevents permanent blocking if bot crashes

#### Files to Create
- `src/auto/farming/scheduler/ResourceLockManager.java`
- `src/auto/farming/scheduler/ResourceLock.java`

---

### Story FB-7.6: Implement Travel Optimization
**Story Points**: 21  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Create `TravelOptimizer.java` class
- [ ] Calculate travel time between fields
- [ ] Factor travel time into scheduling decisions
- [ ] Implement field clustering: batch nearby fields together
- [ ] Calculate optimal field visiting order (TSP-like problem)
- [ ] Cache travel time estimates
- [ ] Use current character position in calculations
- [ ] Bias toward nearby fields when priorities equal
- [ ] Support manual field ordering override

#### Technical Notes
- Traveling salesman problem (TSP) for optimal route
- Use greedy nearest-neighbor for fast approximation
- Pre-calculate field distances at startup
- Consider: is travel time worth optimizing vs just processing by priority?

#### Files to Create
- `src/auto/farming/scheduler/TravelOptimizer.java`

---

### Story FB-7.7: Implement Field Bot Coordination
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Modify QuantityFieldBot and QualityFieldBot to integrate with scheduler
- [ ] Add scheduler callbacks to bots:
  - `onWorkAssigned()`: Scheduler assigns work to bot
  - `onWorkComplete()`: Bot notifies scheduler of completion
  - `onWorkFailed()`: Bot reports error to scheduler
  - `onResourceNeeded()`: Bot requests resource lock
- [ ] Implement cooperative pausing (bots respond to scheduler pause)
- [ ] Handle work interruption (scheduler reassigns higher priority work)
- [ ] Report bot status to scheduler each tick
- [ ] Ensure only one bot active per character at a time

#### Technical Notes
- Bots transition from independent operation to scheduler-driven
- Scheduler may interrupt low-priority work for urgent tasks
- Status reporting enables scheduler to make informed decisions

#### Files to Modify
- `src/auto/farming/bot/QuantityFieldBot.java`
- `src/auto/farming/bot/quality/QualityFieldBot.java`

---

### Story FB-7.8: Implement Global Pause/Resume System
**Story Points**: 8  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Add global pause action to xTended menu (PAUSE_FARMING_BOT)
- [ ] Pause all active field bots when triggered
- [ ] Save current state before pausing
- [ ] Resume all bots from saved state
- [ ] Show paused status in UI and overlay
- [ ] Add auto-pause triggers:
  - Player proximity (if configured)
  - Inventory full across all fields
  - Critical error in any field
  - Manual pause request
- [ ] Add keyboard shortcut (Ctrl+Shift+P)

#### Technical Notes
- Pause should be graceful: finish current action, then pause
- Resume should continue from exact pause point
- Global pause different from per-field pause (affects all fields)

#### Files to Modify
- `src/auto/farming/FarmingScheduler.java`
- `src/haven/Action.java`
- `src/haven/MenuGrid.java`

---

### Story FB-7.9: Add System-Wide Statistics Dashboard
**Story Points**: 21  
**Priority**: P2  

#### Acceptance Criteria
- [ ] Create `FarmingDashboard.java` window (Epic 3 integration)
- [ ] Display global statistics:
  - Total fields (enabled/disabled)
  - Active field count
  - Total crops harvested (all fields)
  - Global average quality (quality fields only)
  - System uptime
  - Actions per minute (throughput)
  - Success rate (actions succeeded / attempted)
- [ ] Show per-field summary table:
  - Field name
  - Status (active/idle/error)
  - Priority
  - Progress (% complete)
  - Last action timestamp
- [ ] Add real-time activity log (recent 50 actions)
- [ ] Support export to CSV/JSON
- [ ] Auto-refresh dashboard (every 5 seconds)

#### Technical Notes
- Dashboard consolidates stats from all field bots
- Useful for monitoring multi-field operation
- Integration point for future external monitoring

#### Files to Create
- `src/auto/farming/ui/FarmingDashboard.java`
- `src/auto/farming/ui/DashboardStatsPanel.java`

---

### Story FB-7.10: Implement Error Propagation & Recovery
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement error handling in scheduler:
  - Field bot error → disable field, alert user, continue with other fields
  - Critical error → pause all bots, notify user
  - Resource lock timeout → release locks, retry or skip field
- [ ] Add error recovery strategies:
  - Retry failed field after delay
  - Auto-disable problematic fields after N failures
  - Attempt automatic fixes (get tools, clear inventory)
- [ ] Emit error notifications to user (system tray, in-game)
- [ ] Log errors for debugging
- [ ] Provide manual error reset/recovery actions

#### Technical Notes
- Scheduler should be resilient: one field error shouldn't crash entire system
- Error notifications should be actionable (tell user what to fix)
- Consider exponential backoff for retry attempts

#### Files to Create
- `src/auto/farming/scheduler/ErrorHandler.java`
- `src/auto/farming/scheduler/RecoveryStrategy.java`

---

### Story FB-7.11: Add Scheduler Configuration Options
**Story Points**: 5  
**Priority**: P2  

#### Acceptance Criteria
- [ ] Add scheduler settings to farming config:
  - Max concurrent fields (default: 1, range: 1-5)
  - Scheduler tick interval (default: 5s, range: 1-60s)
  - Enable travel optimization (default: true)
  - Auto-pause on player proximity (default: false)
  - Priority calculation weights
- [ ] Create scheduler settings UI panel (Epic 3 integration)
- [ ] Apply configuration changes in real-time
- [ ] Persist settings to config file
- [ ] Provide sensible defaults

#### Technical Notes
- Max concurrent fields: advanced users might want parallel processing (risky)
- Most users should stick with sequential processing (max=1)
- Tick interval affects responsiveness vs performance overhead

#### Files to Modify
- `src/auto/farming/config/FarmingConfig.java`
- `src/auto/farming/ui/FarmingConfigWindow.java` (add scheduler settings tab)

---

### Story FB-7.12: Implement Notification System
**Story Points**: 13  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Create `NotificationManager.java` class
- [ ] Support notification types:
  - INFO: General status updates
  - WARNING: Non-critical issues (low seeds, chest nearly full)
  - ERROR: Critical issues requiring intervention
- [ ] Implement notification channels:
  - In-game chat message
  - System tray notification (Windows/Mac/Linux)
  - Audio alert (configurable sound)
  - UI popup window
- [ ] Add notification filtering (user can disable certain types)
- [ ] Implement notification history (last 50 notifications)
- [ ] Rate-limit notifications (don't spam same message)

#### Technical Notes
- System tray notifications useful for AFK monitoring
- Audio alerts configurable: different sounds for different severity
- Notification history viewable in farming dashboard

#### Files to Create
- `src/auto/farming/NotificationManager.java`
- `src/auto/farming/Notification.java` (data class)

---

### Story FB-7.13: Create Multi-Field Integration Tests
**Story Points**: 21  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Test scheduler with 2 fields (1 quantity, 1 quality)
- [ ] Test scheduler with 5+ fields of mixed types
- [ ] Test priority system (high-priority field processed first)
- [ ] Test resource locking (prevent concurrent chest access)
- [ ] Test pause/resume with multiple active fields
- [ ] Test error handling (one field fails, others continue)
- [ ] Test scheduler performance with 10 fields
- [ ] Validate statistics accuracy across multiple fields
- [ ] Run long-term test (24 hours with 5 fields)

#### Technical Notes
- Multi-field tests require complex test setup
- Create test farm with variety of field types and sizes
- Monitor for deadlocks, race conditions, resource conflicts

#### Files to Create
- `test/auto/farming/FarmingSchedulerTest.java`
- `feature/farming-bot/docs/MULTI-FIELD-TESTING.md`

---

## Definition of Done
- [ ] All stories completed and code reviewed
- [ ] Scheduler can manage 10+ fields simultaneously
- [ ] Priority system works correctly
- [ ] Resource locking prevents conflicts
- [ ] Global pause/resume functional
- [ ] Error handling and recovery robust
- [ ] Statistics and dashboard accurate
- [ ] Notifications working across all channels
- [ ] Code follows project style guidelines
- [ ] Integration tests passing
- [ ] System stable for 24+ hour operation with multiple fields

## Dependencies
- **Epic 5**: Quantity field bot
- **Epic 6**: Quality field bot
- **Epic 3**: UI integration for dashboard and settings

## Blocks
- None (this is final major epic)

## Technical Debt
- Travel optimization may need advanced algorithms (currently greedy)
- Concurrent field processing (max > 1) is risky, needs extensive testing
- Notification rate-limiting may need tuning based on user feedback
- Performance optimization for very large farms (20+ fields)

## Risks
- Scheduling complexity may introduce hard-to-debug issues (mitigation: extensive logging)
- Resource locking may cause deadlocks (mitigation: timeouts and deadlock detection)
- Multi-field operation may strain game client (mitigation: performance profiling)

## Testing Strategy
1. Unit tests for priority calculation and work queue
2. Integration tests for scheduler with multiple bots
3. Stress tests with 10+ fields
4. Long-running stability tests (24+ hours)
5. Error injection tests (simulate field failures, resource conflicts)
6. Performance profiling and optimization
7. User acceptance testing with real farming scenarios
