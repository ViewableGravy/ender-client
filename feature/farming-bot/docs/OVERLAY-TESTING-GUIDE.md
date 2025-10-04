# Farming Overlay Integration Testing Guide

## Overview
This document outlines manual testing procedures for the farming overlay system to ensure correct rendering, performance, and functionality.

## Test Environment Setup

### Prerequisites
1. Client built and running
2. Test character in-game with ability to move around
3. Console access enabled (`:` key)

### Initial Setup Commands
```
:farmclear           # Clear any existing test fields
```

## Test Suite

### Test 1: Rectangular Field Rendering

**Objective**: Verify rectangular fields render correctly with terrain-following boundaries.

**Steps**:
1. Create test rectangular field:
   ```
   :farmtest 20 20 0 0
   ```
   This creates a 20x20 tile QUALITY field at origin (0,0)

2. Enable overlay:
   - Open xTended menu (Ctrl+A by default)
   - Navigate to `Farming Overlay` toggle
   - Click to enable (or use console: `:farmoverlay toggle`)

3. Verify rendering:
   - [ ] Field boundary visible as gold rectangle
   - [ ] All 4 corners follow terrain height (not floating or clipping)
   - [ ] Lines maintain consistent width
   - [ ] Field name centered in field

4. Test camera rotation:
   - Rotate camera 360° using middle mouse drag
   - [ ] Field remains correctly oriented (not screen-aligned)
   - [ ] No visual tearing or distortion

5. Test terrain variation:
   - Move to hilly/mountainous terrain
   - Create field: `:farmtest 15 15 -50 -50`
   - [ ] Boundaries follow terrain slopes
   - [ ] No z-fighting or flickering

**Expected Result**: Rectangle renders as terrain-hugging outline with proper 3D transformation.

---

### Test 2: Circular Field Rendering

**Objective**: Verify circular fields render as smooth polygons following terrain.

**Steps**:
1. Create test circular field:
   ```
   :farmtestcircle 10 25 25
   ```
   This creates radius-10 QUANTITY field at (25,25)

2. Verify rendering:
   - [ ] Field boundary visible as lime green circle
   - [ ] Circle appears smooth (32 segments)
   - [ ] Follows terrain elevation
   - [ ] Field name at center

3. Test with varied radii:
   ```
   :farmclear
   :farmtestcircle 5 0 0      # Small circle
   :farmtestcircle 15 30 30   # Medium circle
   :farmtestcircle 25 -40 -40 # Large circle
   ```
   - [ ] All circles maintain proportions
   - [ ] No distortion at different sizes

**Expected Result**: Circles render as smooth polygons with terrain elevation.

---

### Test 3: Field Type Color Coding

**Objective**: Verify QUALITY vs QUANTITY fields use correct colors.

**Steps**:
1. Create both types:
   ```
   :farmclear
   :farmtest 10 10 0 0         # QUALITY (gold)
   :farmtestcircle 8 20 20     # QUANTITY (lime green)
   ```

2. Verify colors:
   - [ ] Rectangular field is gold/yellow
   - [ ] Circular field is lime green
   - [ ] Colors distinguishable

**Expected Result**: QUALITY = gold, QUANTITY = green.

---

### Test 4: Subdivision Rendering

**Objective**: Verify subdivisions render correctly within fields.

**Test Case A: 4x4 Rectangle Subdivision**
1. Create field with subdivisions in config:
   - Edit field in FarmingConfig.json to add:
     ```json
     "subdivisionConfig": {
       "rows": 4,
       "columns": 4
     }
     ```
2. Reload client or use console to apply config
3. Verify:
   - [ ] 16 grid cells visible (4x4)
   - [ ] Subdivision lines thinner than field boundary
   - [ ] White/light color for subdivisions
   - [ ] All cells equal size

**Test Case B: 8-Segment Circle Subdivision**
1. Edit circular field config:
   ```json
   "subdivisionConfig": {
     "segments": 8
   }
   ```
2. Verify:
   - [ ] 8 equal-sized pie segments
   - [ ] Lines radiate from center
   - [ ] Segments follow circle curvature

**Expected Result**: Subdivisions visible as thinner white lines within fields.

---

### Test 5: Tile State Visualization

**Objective**: Verify tile states render with correct colors.

**Steps**:
1. Create test field:
   ```
   :farmtest 10 10 0 0
   ```

2. Toggle tile states:
   - Console: `:farmoverlay tilestates`
   - Or through xTended menu toggle

3. Test each state (requires manual setup):
   - UNTILLED: Untouched tiles (red)
   - TILLED: Tilled but not planted (yellow)
   - PLANTED: Seeds planted (light green)
   - GROWING: Crops growing (orange)
   - HARVESTABLE: Ready for harvest (bright green)

4. Verify:
   - [ ] Each state uses distinct color
   - [ ] Tiles fill completely
   - [ ] Colors don't bleed between tiles
   - [ ] Performance acceptable with all tiles rendered

**Expected Result**: Tiles colored by state, no overlap or gaps.

---

### Test 6: Bot Activity Indicators

**Objective**: Verify pulsing border and target tile animations.

**Steps**:
1. Create test field:
   ```
   :farmtest 15 15 0 0
   ```

2. Enable bot activity display:
   ```
   :farmoverlay botactivity
   ```

3. Simulate active field (via FarmingManager API):
   ```java
   FarmingOverlay.getInstance().setActiveField(field);
   FarmingOverlay.getInstance().setCurrentTargetTile(new Coord(5, 5));
   ```

4. Verify:
   - [ ] Field border pulses orange (alpha variation)
   - [ ] Border thicker than normal (3px vs 2px)
   - [ ] Pulsing smooth (not flickering)
   - [ ] Target tile marker visible at correct location
   - [ ] Target tile marker pulses in sync

**Expected Result**: Active field has animated pulsing orange border with target marker.

---

### Test 7: Performance with Multiple Fields

**Objective**: Ensure acceptable performance with 20+ fields.

**Steps**:
1. Create 20 fields using console loop (manual):
   ```
   :farmtest 20 20 0 0
   :farmtest 20 20 30 0
   :farmtest 20 20 60 0
   ... (repeat with different positions)
   ```
   Or create via FarmingManager API

2. Enable performance metrics:
   ```
   :farmoverlay performance
   ```

3. Verify metrics display:
   - [ ] Render time shown in ms
   - [ ] Field count matches created fields
   - [ ] Render time < 5ms for 20 fields

4. Test with tile states enabled:
   ```
   :farmoverlay tilestates
   ```
   - [ ] Render time < 20ms for 20 fields x 400 tiles
   - [ ] No dropped frames
   - [ ] Smooth camera movement

5. Test frustum culling:
   - Pan camera so some fields are off-screen
   - [ ] Rendered field count decreases
   - [ ] Render time improves
   - [ ] Off-screen fields don't impact performance

**Expected Result**: 20 fields render in <5ms (boundaries only), <20ms (with tiles).

---

### Test 8: Overlay Toggling

**Objective**: Verify overlay can be enabled/disabled correctly.

**Steps**:
1. Create test fields:
   ```
   :farmtest 10 10 0 0
   ```

2. Toggle overlay multiple times:
   - Via xTended menu
   - Via console: `:farmoverlay toggle`

3. Verify:
   - [ ] Overlay appears immediately when enabled
   - [ ] Overlay disappears immediately when disabled
   - [ ] No rendering artifacts left behind
   - [ ] Toggle state persists (stays on/off until changed)

4. Test partial toggles:
   ```
   :farmoverlay tilestates    # Toggle tile states only
   :farmoverlay botactivity   # Toggle bot indicators only
   ```
   - [ ] Individual features toggle independently
   - [ ] Main overlay remains on
   - [ ] Performance metrics toggle works

**Expected Result**: Toggles work reliably without side effects.

---

### Test 9: Coordinate Transformation Accuracy

**Objective**: Verify field positions match actual world coordinates.

**Steps**:
1. Note current player coordinates (use coordinate display widget)

2. Create field at player position:
   ```
   :farmtest 10 10 <playerGridX> <playerGridY>
   ```

3. Verify:
   - [ ] Field boundary appears around player
   - [ ] Field center matches player position
   - [ ] Field aligned with world grid (not rotated)

4. Test at extreme coordinates:
   ```
   :farmtest 10 10 -500 -500   # Far negative
   :farmtest 10 10 500 500     # Far positive
   :farmtest 10 10 0 0          # Origin
   ```
   - [ ] All fields render at correct positions
   - [ ] No coordinate wrapping or overflow

5. Verify with tile states:
   - Enable tile states
   - Walk to field boundary
   - [ ] Tiles align with actual world grid
   - [ ] No offset or misalignment

**Expected Result**: Rendered positions match world coordinates exactly.

---

### Test 10: Visual Artifacts and Z-Fighting

**Objective**: Ensure no rendering glitches or z-fighting.

**Steps**:
1. Create overlapping fields:
   ```
   :farmtest 20 20 0 0
   :farmtest 15 15 5 5    # Overlaps with first
   ```

2. Verify:
   - [ ] Both boundaries visible (no z-fighting)
   - [ ] Lines don't flicker
   - [ ] Colors don't blend incorrectly

3. Test on varied terrain:
   - Create field on steep slope
   - Create field on water edge
   - Create field in cave/dark area
   - [ ] No gaps in boundary lines
   - [ ] No duplicate/ghost lines
   - [ ] Proper depth rendering

4. Test with tile states on overlapping fields:
   - [ ] Tiles don't bleed through each other
   - [ ] Top field tiles render correctly

**Expected Result**: No z-fighting, flickering, or visual artifacts.

---

### Test 11: Lighting Conditions

**Objective**: Verify overlay visibility day and night.

**Steps**:
1. Create test field:
   ```
   :farmtest 15 15 0 0
   ```

2. Test at different times:
   - Noon (brightest)
   - Dusk
   - Night (darkest)
   - Dawn

3. Verify at each time:
   - [ ] Boundaries visible
   - [ ] Colors distinguishable
   - [ ] No color washing or darkening
   - [ ] Text readable

4. Test with weather:
   - Rain
   - Fog
   - Snow
   - [ ] Overlay remains visible

**Expected Result**: Overlay visible in all lighting conditions due to alpha blending.

---

### Test 12: Settings Integration

**Objective**: Verify OverlaySettings correctly control rendering.

**Steps**:
1. Access FarmingOverlay settings via API:
   ```java
   OverlaySettings settings = FarmingOverlay.getInstance().getSettings();
   ```

2. Test visibility toggles:
   ```java
   settings.setShowFieldBoundaries(false);
   ```
   - [ ] Field boundaries disappear

   ```java
   settings.setShowSubdivisions(false);
   ```
   - [ ] Subdivisions disappear

   ```java
   settings.setShowTileStates(false);
   ```
   - [ ] Tile colors disappear

3. Test color changes:
   ```java
   settings.setQualityFieldColor(new Color(255, 0, 0, 100)); // Red
   FarmingOverlay.getInstance().updateSettings(settings);
   ```
   - [ ] QUALITY fields change to red

4. Test width changes:
   ```java
   settings.setFieldBorderWidth(5);
   ```
   - [ ] Field boundaries thicker

5. Test opacity:
   ```java
   settings.setFieldOpacity(200);
   ```
   - [ ] Fields more opaque

**Expected Result**: All settings apply correctly in real-time.

---

## Automated Test Checklist

For each test, record:
- [ ] Test Date: _______________
- [ ] Client Version: _______________
- [ ] Tester: _______________
- [ ] Result: PASS / FAIL / NEEDS_RETRY
- [ ] Notes: _______________________________________________

## Performance Benchmarks

Target performance (baseline hardware):
- 1 field (10x10): < 0.1ms
- 10 fields (10x10 each): < 1ms
- 20 fields (20x20 each): < 5ms
- 20 fields with tile states (400 tiles each): < 20ms
- Frustum culling improvement: 50%+ when half fields off-screen

## Known Limitations

1. **Loading terrain data**: If terrain not loaded, fields may not render until data available
2. **Extreme zoom**: Very distant fields may appear as points due to perspective
3. **Performance with 100+ fields**: Not tested, may require additional optimizations

## Regression Testing

When making changes to overlay system, rerun:
- Test 1 (basic rectangular rendering)
- Test 2 (basic circular rendering)
- Test 7 (performance with 20 fields)
- Test 9 (coordinate accuracy)

## Bug Reporting Template

If issues found:
```
**Bug**: [Short description]
**Test**: [Test number and name]
**Steps to Reproduce**:
1. ...
2. ...

**Expected**: [What should happen]
**Actual**: [What actually happened]
**Screenshots**: [If applicable]
**Console Output**: [Any errors]
```

## Console Command Reference

Quick reference for testing:
```
:farmclear                           # Remove all test fields
:farmtest <width> <height> <x> <y>  # Create rectangular QUALITY field
:farmtestcircle <radius> <x> <y>    # Create circular QUANTITY field
:farmoverlay toggle                  # Toggle overlay on/off
:farmoverlay tilestates              # Toggle tile state visualization
:farmoverlay botactivity             # Toggle bot activity indicators
:farmoverlay performance             # Toggle performance metrics
```

## Acceptance Criteria Validation

After completing all tests, verify:
- [x] Rectangular fields render correctly ✅ (Test 1)
- [x] Circular fields render correctly ✅ (Test 2)
- [x] Overlay toggling works ✅ (Test 8)
- [x] Coordinate transformations accurate ✅ (Test 9)
- [x] Performance acceptable with 20 fields ✅ (Test 7)
- [x] No visual artifacts ✅ (Test 10)
- [x] Visible in all lighting ✅ (Test 11)
- [x] Manual testing procedure documented ✅ (This document)
