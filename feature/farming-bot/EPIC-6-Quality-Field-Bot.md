# Epic 6: Quality Field Bot Implementation

**Epic ID**: FB-EPIC-6  
**Status**: Not Started  
**Priority**: P0 (Blocker)  
**Estimate**: 2 weeks  
**Dependencies**: Epic 1, Epic 4 (Farming Actions), Epic 5 (Quantity Bot patterns)  

## Epic Description
Implement the autonomous bot for quality-focused fields that prioritizes breeding crops to increase quality over time. The bot will selectively plant high-quality seeds, inspect crop quality, transplant or remove low-quality crops to quantity fields, and maintain quality thresholds to progressively improve the field's average quality.

## Goals
- Implement selective breeding algorithm (0 to +5 quality improvement)
- Track and maintain field average quality
- Transplant/discard low-quality crops
- Intelligent seed selection for quality improvement
- Manage quality field lifecycle with inspections
- Support quality progression over multiple generations
- Integrate with quantity fields for low-quality crop disposal

## User Stories

---

### Story FB-6.1: Design Quality Breeding Algorithm
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Research crop quality genetics in Haven & Hearth
- [ ] Design algorithm for quality improvement:
  - Calculate current field average quality
  - Determine target quality (current avg + 1 to +5)
  - Select seeds within acceptable range (0 to +5 from target)
  - Reject seeds below threshold (replant or move to quantity field)
- [ ] Document breeding strategy and expected progression
- [ ] Define quality threshold formula
- [ ] Create simulation to validate algorithm effectiveness
- [ ] Plan for quality plateau handling (when improvement stalls)

#### Technical Notes
- Quality inheritance: crops grown near higher quality crops tend toward higher quality
- Quality range: ±5 from surrounding crops (research needed to confirm)
- Strategy: maintain minimum quality, gradually increase threshold
- Example progression: Avg Q10 → plant Q10-15 → harvest Q12-17 → plant Q15-20 → etc.

#### Files to Create
- `feature/farming-bot/docs/QUALITY-BREEDING-ALGORITHM.md`
- `src/auto/farming/bot/quality/QualityBreedingStrategy.java`

---

### Story FB-6.2: Create Quality Field Bot Core
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `QualityFieldBot.java` class (similar to QuantityFieldBot)
- [ ] Implement quality-specific state machine:
  - INITIALIZING: Scan field and calculate current quality stats
  - INSPECTING: Check quality of growing crops
  - CULLING: Remove/transplant low-quality crops
  - TILLING: Prepare tiles for new planting
  - PLANTING: Plant high-quality seeds selectively
  - GROWING: Wait for crops to mature
  - HARVESTING: Collect mature crops
  - SORTING: Separate high/low quality harvest
  - STORING: Store crops by quality tier
  - RESTOCKING: Get high-quality seeds
  - OPTIMIZING: Adjust quality thresholds
  - IDLE / ERROR
- [ ] Implement bot lifecycle (start, pause, resume, stop)
- [ ] Run on separate thread
- [ ] Emit detailed status for quality tracking

#### Technical Notes
```java
package auto.farming.bot.quality;

public class QualityFieldBot {
    private final FarmField field;
    private QualityFieldState state;
    private QualityTracker qualityTracker;
    private double currentAvgQuality;
    private double targetMinQuality;
    
    // Similar structure to QuantityFieldBot
    // But with quality-aware decision making
}
```

#### Files to Create
- `src/auto/farming/bot/quality/QualityFieldBot.java`
- `src/auto/farming/bot/quality/QualityFieldState.java` (enum)

---

### Story FB-6.3: Implement Quality Tracking System
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `QualityTracker.java` class
- [ ] Track quality for each tile in field:
  - Current planted crop quality
  - Historical quality (last N harvests)
  - Quality trend (improving/declining/stable)
- [ ] Calculate field-wide statistics:
  - Average quality
  - Median quality
  - Quality standard deviation
  - Min/max quality
- [ ] Maintain quality history over time (per cycle)
- [ ] Provide quality visualization data for overlay
- [ ] Persist quality data in field config

#### Technical Notes
- Quality data structure: Map<Coord2d, List<Integer>> for historical quality
- Statistics recalculated after each harvest
- Quality trends help identify problem areas in field
- Visualization: heat map overlay showing quality distribution

#### Files to Create
- `src/auto/farming/bot/quality/QualityTracker.java`
- `src/auto/farming/bot/quality/QualityStatistics.java`

---

### Story FB-6.4: Implement Crop Inspection Logic
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `handleInspecting()` state handler
- [ ] Scan field for planted (but not yet harvestable) crops
- [ ] Inspect quality of each growing crop using `CropInspector`
- [ ] Build inspection queue (prioritize crops near harvest)
- [ ] Execute inspection actions (may require right-click inspect)
- [ ] Record inspected quality in `QualityTracker`
- [ ] Identify low-quality crops for culling
- [ ] Transition to CULLING if low-quality crops found
- [ ] Transition to GROWING if all crops acceptable

#### Technical Notes
- May need to physically inspect each crop (time-consuming)
- Inspection might only be possible at certain growth stages
- Alternative: inspect seeds before planting instead of crops after
- Prioritize inspection of crops nearing maturity

#### Files to Modify
- `src/auto/farming/bot/quality/QualityFieldBot.java`

---

### Story FB-6.5: Implement Crop Culling & Transplanting
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `handleCulling()` state handler
- [ ] Identify crops below quality threshold
- [ ] Decide action per low-quality crop:
  - REMOVE: Harvest and discard/move to quantity field
  - TRANSPLANT: Move to quantity field for continued growth
  - WAIT: Allow to grow (if quality borderline)
- [ ] Execute removal actions (harvest early or destroy)
- [ ] For transplanting: coordinate with quantity field bot
- [ ] Update tile state after culling
- [ ] Re-till culled tiles for replanting
- [ ] Transition to PLANTING when culling complete
- [ ] Track culling statistics (culled count, avg quality removed)

#### Technical Notes
- Transplanting requires integration with quantity field system
- Early harvest may yield partial crops (handle reduced yield)
- Culling threshold: crops below (field avg - 3) or below absolute minimum
- Consider cost/benefit: is culling worth the effort vs letting crop mature?

#### Files to Modify
- `src/auto/farming/bot/quality/QualityFieldBot.java`

#### Files to Create
- `src/auto/farming/bot/quality/CullingDecision.java` (enum/class)

---

### Story FB-6.6: Implement Quality-Aware Seed Selection
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `QualitySeedSelector.java` class
- [ ] Extend `SeedInventoryManager` with quality-specific logic
- [ ] Filter seeds by quality range (target avg ± quality tolerance)
- [ ] Prefer seeds in range: [target_avg, target_avg + 5]
- [ ] Reject seeds below minimum threshold (target_avg - 1)
- [ ] Sort seeds by quality (highest first)
- [ ] Handle insufficient high-quality seeds:
  - Use best available (even if below ideal)
  - Request restocking of high-quality seeds
  - Optionally pause planting until quality seeds available
- [ ] Track seed quality usage for statistics

#### Technical Notes
```java
// Example: Field avg quality = 15
// Target range: Q15 - Q20 (ideal)
// Acceptable range: Q14 - Q20
// Reject: Q < 14

QualitySeedSelector selector = new QualitySeedSelector(field);
selector.setTargetQuality(15);
selector.setQualityTolerance(1); // Accept Q14-Q20
GItem seed = selector.selectBestSeed("wheat");
```

#### Files to Create
- `src/auto/farming/bot/quality/QualitySeedSelector.java`

---

### Story FB-6.7: Implement Quality-Aware Planting
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `handlePlanting()` for quality fields
- [ ] Use `QualitySeedSelector` to choose seeds
- [ ] Only plant if seed quality meets threshold
- [ ] Skip tiles if no suitable seeds (leave tilled for next cycle)
- [ ] Record planted seed quality per tile
- [ ] Update quality tracker with planted quality
- [ ] Calculate expected quality progression
- [ ] Transition to GROWING when planting complete or seeds exhausted
- [ ] Transition to RESTOCKING if need more high-quality seeds

#### Technical Notes
- Partial planting acceptable: better to wait for quality seeds than plant poor ones
- Track which tiles planted this cycle for harvest planning
- Consider planting pattern: does positioning affect quality outcome?

#### Files to Modify
- `src/auto/farming/bot/quality/QualityFieldBot.java`

---

### Story FB-6.8: Implement Quality-Based Harvesting
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `handleHarvesting()` for quality fields
- [ ] Inspect crop quality before harvesting (if not done earlier)
- [ ] Harvest all mature crops
- [ ] Record harvested quality per tile
- [ ] Update quality tracker with harvest results
- [ ] Separate harvest into quality tiers:
  - High quality (above avg + 3): save as breeding seeds
  - Medium quality (avg ± 3): store for use/sale
  - Low quality (below avg - 3): move to quantity field storage
- [ ] Transition to SORTING when inventory full or all harvested
- [ ] Update field average quality after harvest

#### Technical Notes
- Post-harvest quality inspection critical for tracking improvement
- High-quality harvest becomes next generation's seeds
- Quality progression validation: compare this harvest to previous

#### Files to Modify
- `src/auto/farming/bot/quality/QualityFieldBot.java`

---

### Story FB-6.9: Implement Quality-Based Storage Sorting
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `handleSorting()` state handler
- [ ] Navigate to storage chests (multiple chests for different quality tiers)
- [ ] Sort harvested crops by quality:
  - Premium chest: Q > (avg + 3) - breeding stock
  - Main chest: Q = (avg ± 3) - primary storage
  - Quantity chest: Q < (avg - 3) - move to quantity field system
- [ ] Transfer crops to appropriate chests
- [ ] Handle chest full scenarios (use next chest or alert)
- [ ] Reserve high-quality crops as seeds for next planting
- [ ] Transition to OPTIMIZING or TILLING for next cycle
- [ ] Update storage statistics

#### Technical Notes
- Quality-based sorting requires multiple chest links (configured in Epic 3)
- Premium breeding stock should be clearly separated
- Consider keeping some high-quality crops for consumption vs all for breeding

#### Files to Modify
- `src/auto/farming/bot/quality/QualityFieldBot.java`

#### Files to Create
- `src/auto/farming/bot/quality/QualitySorter.java`

---

### Story FB-6.10: Implement Quality Threshold Optimization
**Story Points**: 13  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Implement `handleOptimizing()` state handler
- [ ] Analyze previous cycle results:
  - Average quality change (harvest vs planted)
  - Quality improvement rate
  - Percentage of crops culled
  - Seed availability at target quality
- [ ] Adjust quality thresholds for next cycle:
  - If quality improving consistently: increase min threshold
  - If too many crops culled: lower threshold slightly
  - If quality stagnant: maintain current threshold
  - If insufficient seeds: lower threshold temporarily
- [ ] Calculate optimal threshold based on seed availability
- [ ] Set new thresholds in `QualityTracker`
- [ ] Transition to IDLE or TILLING for next cycle
- [ ] Log optimization decisions

#### Technical Notes
- Optimization balances quality improvement vs practical constraints
- Aggressive thresholds → faster quality gain but more culling
- Conservative thresholds → slower improvement but less waste
- Adaptive algorithm should find optimal balance over time

#### Files to Modify
- `src/auto/farming/bot/quality/QualityFieldBot.java`

#### Files to Create
- `src/auto/farming/bot/quality/ThresholdOptimizer.java`

---

### Story FB-6.11: Implement Quality Field Restocking
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Implement `handleRestocking()` for quality fields
- [ ] Navigate to seed storage (premium breeding stock chest)
- [ ] Withdraw high-quality seeds (above current threshold)
- [ ] Fallback to main storage if premium insufficient
- [ ] Validate seed quality before taking (don't take low-quality)
- [ ] Calculate needed quantity based on empty tiles
- [ ] Handle no suitable seeds scenario:
  - Alert user
  - Optionally wait for user to add seeds
  - Optionally lower threshold and use available seeds
- [ ] Return to field after restocking
- [ ] Transition to PLANTING

#### Technical Notes
- Premium seed chest should be restocked from previous harvests
- If breeding stock depleted, may need to "import" from another quality field
- Consider seed buffer: always keep some high-quality seeds in reserve

#### Files to Modify
- `src/auto/farming/bot/quality/QualityFieldBot.java`

---

### Story FB-6.12: Add Quality Progression Visualization
**Story Points**: 13  
**Priority**: P2  

#### Acceptance Criteria
- [ ] Add quality heat map to overlay (Epic 2 integration)
- [ ] Color-code tiles by planted/harvested quality:
  - Dark green: High quality (Q > avg + 3)
  - Green: Above average (Q = avg + 1 to avg + 3)
  - Yellow: Average (Q = avg ± 1)
  - Orange: Below average (Q = avg - 3 to avg - 1)
  - Red: Low quality (Q < avg - 3)
- [ ] Show field average quality in overlay label
- [ ] Display quality trend arrow (↑↓→)
- [ ] Add quality progression graph in UI window
- [ ] Show historical quality over cycles (line chart)

#### Technical Notes
- Heat map useful for identifying quality problem areas
- Progression graph validates breeding algorithm effectiveness
- Integration with Epic 2 overlay system and Epic 3 UI window

#### Files to Create
- `src/auto/farming/ui/overlay/QualityHeatMapRenderer.java`
- `src/auto/farming/ui/QualityProgressionChart.java`

---

### Story FB-6.13: Implement Integration with Quantity Fields
**Story Points**: 13  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Create mechanism for quality bot to transfer low-quality crops to quantity fields
- [ ] Implement "dump to quantity" workflow:
  - Identify designated quantity field for low-quality crops
  - Transfer culled/low-quality crops to quantity field storage
  - Optionally plant low-quality crops in quantity field if space available
- [ ] Coordinate between quality and quantity bots (avoid conflicts)
- [ ] Track cross-field transfers in statistics
- [ ] Support configuring quality→quantity field relationships

#### Technical Notes
- Quality field can "feed" quantity field with culled crops
- Quantity field benefits from quality field's rejects
- Need coordination to avoid both bots accessing same chest simultaneously
- Configuration: link quality field to specific quantity field for transfers

#### Files to Create
- `src/auto/farming/bot/FieldCoordinator.java`

---

### Story FB-6.14: Create Quality Bot Tests
**Story Points**: 21  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Test quality breeding algorithm with simulated data
- [ ] Test crop inspection and culling logic
- [ ] Test quality-aware seed selection
- [ ] Test quality-based storage sorting
- [ ] Test threshold optimization over multiple cycles
- [ ] Test integration with quantity fields
- [ ] Run long-term test: 20 cycles, validate quality progression
- [ ] Document expected quality improvement rates

#### Technical Notes
- Simulation can test algorithm without live game
- Live testing required for full integration validation
- Track quality progression over 20+ cycles to validate algorithm

#### Files to Create
- `test/auto/farming/bot/quality/QualityBreedingAlgorithmTest.java`
- `test/auto/farming/bot/quality/QualityFieldBotTest.java`
- `feature/farming-bot/docs/QUALITY-BOT-TESTING.md`

---

## Definition of Done
- [ ] All stories completed and code reviewed
- [ ] Quality bot can autonomously improve field quality over multiple cycles
- [ ] Crop inspection, culling, and selective planting functional
- [ ] Quality tracking and statistics accurate
- [ ] Storage sorting by quality works correctly
- [ ] Threshold optimization improves quality progression
- [ ] Integration with quantity fields functional
- [ ] Code follows project style guidelines
- [ ] Tests validate quality improvement algorithm
- [ ] Bot stable for long-term operation (20+ cycles)

## Dependencies
- **Epic 1**: Field data structures
- **Epic 4**: Farming actions, crop quality inspection
- **Epic 5**: Patterns from quantity bot (state machine, inventory management)
- **Epic 3**: Quality-based storage configuration

## Blocks
- **Epic 7**: Multi-field scheduler needs both quantity and quality bots complete

## Technical Debt
- Quality genetics may be more complex than assumed (requires research)
- Threshold optimization algorithm may need tuning based on real data
- Crop inspection may be slow for large fields (optimization needed)
- Transplanting mechanics may not exist in game (alternative: just harvest/replant)

## Risks
- Quality breeding may not work as expected (mitigation: early testing and algorithm adjustment)
- Inspection overhead may slow down bot significantly (mitigation: selective inspection strategies)
- Quality improvement may plateau quickly (mitigation: adaptive thresholds)

## Testing Strategy
1. Unit tests for quality calculations and algorithms
2. Simulation tests for breeding strategy validation
3. Integration tests with live game for inspection/culling
4. Long-term progression tests (20+ cycles)
5. Compare quality bot vs manual farming efficiency
6. A/B test different threshold strategies
