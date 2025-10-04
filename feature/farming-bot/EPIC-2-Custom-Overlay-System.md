# Epic 2: Custom Overlay System

**Epic ID**: FB-EPIC-2  
**Status**: Not Started  
**Priority**: P0 (Blocker)  
**Estimate**: 1.5 weeks  
**Dependencies**: Epic 1 (Field Data Structures)  

## Epic Description
Implement a custom overlay rendering system in MapView that displays field boundaries, subdivisions, tile states, and bot status in real-time. The overlay should be visible only to the local player and not interfere with normal gameplay.

## Goals
- Render field boundaries on the game map
- Display field subdivisions with distinct colors
- Show tile states (tilled, planted, harvestable, etc.)
- Indicate current bot activity (which field/tile being processed)
- Support toggling overlay visibility
- Optimize rendering performance for large fields
- Integrate with existing MapView rendering pipeline

## User Stories

---

### Story FB-2.1: Research MapView Rendering System
**Story Points**: 5  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Document how MapView rendering works (rendering phases, coordinate system)
- [ ] Identify where to hook custom overlay rendering
- [ ] Study minesweeper overlay implementation (`MineSweeper.java`)
- [ ] Understand camera/coordinate transformations
- [ ] Identify OpenGL/JOGL drawing utilities available
- [ ] Document performance considerations and best practices
- [ ] Create technical design document for overlay integration

#### Technical Notes
- Reference files:
  - `src/haven/MapView.java` - Main map rendering
  - `src/me/ender/minimap/MineSweeper.java` - Example overlay
  - `src/haven/GOut.java` - Graphics output utilities
- MapView uses camera transformations, need to convert grid coords to screen coords
- Rendering should happen after terrain but before UI elements

#### Files to Create
- `feature/farming-bot/docs/OVERLAY-RENDERING-DESIGN.md`

---

### Story FB-2.2: Create Overlay Renderer Base Class
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `FarmingOverlay.java` class in `src/auto/farming/ui/overlay/`
- [ ] Extend or integrate with MapView rendering pipeline
- [ ] Implement base rendering loop called each frame
- [ ] Add enable/disable toggle functionality
- [ ] Convert grid coordinates to world/screen coordinates
- [ ] Implement camera culling (don't render off-screen fields)
- [ ] Add debug mode for performance profiling
- [ ] Ensure overlay renders above terrain but below UI widgets

#### Technical Notes
```java
package auto.farming.ui.overlay;

public class FarmingOverlay {
    private boolean enabled = false;
    private final FarmingManager manager;
    
    public void render(GOut g, MapView mapView) {
        if (!enabled) return;
        
        // Get visible fields
        // Transform coordinates
        // Draw field boundaries
        // Draw tile states
    }
    
    public void toggle() {
        enabled = !enabled;
    }
}
```

#### Files to Create
- `src/auto/farming/ui/overlay/FarmingOverlay.java`
- `src/auto/farming/ui/overlay/OverlayRenderer.java` (interface)

---

### Story FB-2.3: Implement Field Boundary Rendering
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Render rectangular field boundaries as outlined boxes
- [ ] Render circular field boundaries as circles
- [ ] Use distinct colors for QUALITY vs QUANTITY fields
- [ ] Add field name label above each field
- [ ] Support line thickness configuration
- [ ] Handle partially visible fields at screen edges
- [ ] Add opacity/transparency support
- [ ] Implement hover highlighting (brighter when mouse over field)

#### Technical Notes
- Color scheme:
  - QUALITY fields: Green (#00FF00) with 50% opacity
  - QUANTITY fields: Blue (#4DA6FF) with 50% opacity
  - Disabled fields: Gray (#808080) with 30% opacity
  - Hovered field: +30% brightness
- Use OpenGL line drawing primitives
- Field name rendering uses existing font system

#### Files to Create
- `src/auto/farming/ui/overlay/FieldBoundaryRenderer.java`
- `src/auto/farming/ui/overlay/OverlayColors.java` (color constants)

---

### Story FB-2.4: Implement Grid Subdivision Rendering
**Story Points**: 8  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Render subdivision boundaries within fields
- [ ] Use lighter/dashed lines for subdivisions vs field boundaries
- [ ] Show different colors for different crop types in subdivisions
- [ ] Add subdivision labels (e.g., "Q1", "Q2", "Q3", "Q4")
- [ ] Support toggling subdivision visibility independently
- [ ] Handle overlapping subdivision lines cleanly

#### Technical Notes
- Subdivision lines should be thinner and semi-transparent
- Labels should be smaller than field names
- For circles: draw radial lines from center to edge
- For rectangles: draw vertical/horizontal dividing lines

#### Files to Create
- `src/auto/farming/ui/overlay/SubdivisionRenderer.java`

---

### Story FB-2.5: Implement Tile State Visualization
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Render individual tiles with color-coded states:
  - UNTILLED: Dark brown (#4A3728)
  - TILLED: Light brown (#8B7355)
  - PLANTED: Light green (#90EE90)
  - GROWING: Yellow-green gradient by stage (#ADFF2F -> #32CD32)
  - HARVESTABLE: Bright green (#00FF00)
  - ERROR: Red (#FF0000)
- [ ] Support toggling tile state overlay independently
- [ ] Optimize rendering for fields with 500+ tiles
- [ ] Use batch rendering for performance
- [ ] Add LOD (Level of Detail): simplify rendering when zoomed out
- [ ] Show crop quality as text overlay on tiles (optional toggle)
- [ ] Animate growing stages (subtle pulsing effect)

#### Technical Notes
- Each tile is 11x11 units in Haven's coordinate system
- Use filled rectangles for tile backgrounds
- Consider using texture atlas for repeated tile graphics
- Batch all tile drawing into single OpenGL call for performance
- LOD thresholds: 
  - Zoom level > 2.0: Full detail with quality text
  - Zoom level 1.0-2.0: Colored tiles only
  - Zoom level < 1.0: Skip tile rendering, show field summary only

#### Files to Create
- `src/auto/farming/ui/overlay/TileStateRenderer.java`
- `src/auto/farming/ui/overlay/TileColors.java`

---

### Story FB-2.6: Implement Bot Activity Indicators
**Story Points**: 13  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Highlight currently active field with pulsing border
- [ ] Show target tile with animated marker (arrow or circle)
- [ ] Display bot status text overlay:
  - "Tilling..." / "Planting..." / "Harvesting..."
  - Current field name
  - Progress: "X / Y tiles complete"
- [ ] Add path visualization (line from character to target tile)
- [ ] Show queued tiles with subtle indicators
- [ ] Support toggling activity indicators independently

#### Technical Notes
- Use smooth animations (sine wave for pulsing effect)
- Activity indicators should be most visible (highest z-order)
- Path visualization helps debugging pathfinding issues
- Update activity state from bot thread safely (thread-safe reads)

#### Files to Create
- `src/auto/farming/ui/overlay/BotActivityRenderer.java`

---

### Story FB-2.7: Add Overlay Configuration Panel
**Story Points**: 13  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Create overlay settings sub-panel in farming config window
- [ ] Add toggles for:
  - Enable/disable entire overlay
  - Show field boundaries
  - Show subdivisions
  - Show tile states
  - Show quality text
  - Show bot activity
  - Show field labels
- [ ] Add color customization options
- [ ] Add opacity sliders (0-100%)
- [ ] Add line thickness sliders
- [ ] Save settings to farming config JSON
- [ ] Apply settings changes in real-time

#### Technical Notes
- Settings stored in `FarmingConfig` under `overlaySettings` section
- Use existing widget components (Checkboxes, Sliders from `haven` package)
- Apply observer pattern: settings changes trigger overlay re-render

#### Files to Create
- `src/auto/farming/ui/OverlaySettingsPanel.java`
- Update `src/auto/farming/config/FarmingConfig.java` to include overlay settings

---

### Story FB-2.8: Implement Performance Optimization
**Story Points**: 13  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Implement spatial culling: only render visible fields
- [ ] Add object pooling for frequently created objects (coordinates, colors)
- [ ] Batch OpenGL draw calls to minimize state changes
- [ ] Implement dirty flag system: only re-render on changes
- [ ] Cache transformed coordinates between frames
- [ ] Profile rendering performance (target: <5ms per frame for 10 fields)
- [ ] Add performance metrics overlay (FPS, render time) in debug mode
- [ ] Optimize for large fields (1000+ tiles) without FPS drop

#### Technical Notes
- Use viewport frustum culling to skip off-screen fields
- Consider using VBOs (Vertex Buffer Objects) for static geometry
- Profile with JProfiler or VisualVM
- Target: 60 FPS even with overlay enabled on large farm

#### Files to Create
- `src/auto/farming/ui/overlay/RenderOptimizer.java`
- `src/auto/farming/ui/overlay/PerformanceMonitor.java`

---

### Story FB-2.9: Add Overlay Toggle Action to xTended Menu
**Story Points**: 3  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Add `TOGGLE_FARMING_OVERLAY` action to `Action.java` enum
- [ ] Register action in `MenuGrid.initCustomPaginae()`
- [ ] Create pagina resource at `resources/src/local/paginae/add/farming/overlay`
- [ ] Action toggles overlay visibility
- [ ] Action shows ON/OFF state indicator on menu button
- [ ] Add keyboard shortcut (configurable, default: Ctrl+Shift+F)
- [ ] Add audio feedback on toggle (use existing sound system)

#### Technical Notes
```java
// In Action.java
TOGGLE_FARMING_OVERLAY(gui -> {
    FarmingManager.getInstance().getOverlay().toggle();
}, "Toggle Farming Overlay", "Show/hide farming field overlays")
```

#### Files to Modify
- `src/haven/Action.java`
- `src/haven/MenuGrid.java`

#### Files to Create
- `resources/src/local/paginae/add/farming/overlay.png` (icon)

---

### Story FB-2.10: Create Overlay Integration Tests
**Story Points**: 8  
**Priority**: P2  

#### Acceptance Criteria
- [ ] Create visual test for rendering rectangular fields
- [ ] Create visual test for rendering circular fields
- [ ] Test overlay toggling functionality
- [ ] Test coordinate transformation accuracy
- [ ] Test performance with 20 fields of 500 tiles each
- [ ] Verify no visual artifacts or z-fighting
- [ ] Test overlay visibility in different lighting conditions (day/night)
- [ ] Document manual testing procedure

#### Technical Notes
- Create test fields in various locations and sizes
- Use screenshot comparison for visual regression testing
- Profile frame time with overlay enabled vs disabled

#### Files to Create
- `test/auto/farming/ui/overlay/OverlayRenderTest.java`
- `feature/farming-bot/docs/OVERLAY-TESTING-GUIDE.md`

---

## Definition of Done
- [ ] All stories completed and code reviewed
- [ ] Overlay renders correctly for all field types
- [ ] Performance meets targets (<5ms render time, 60 FPS)
- [ ] Toggle action works in xTended menu with state indicator
- [ ] Settings persist across client restarts
- [ ] No visual artifacts or rendering glitches
- [ ] Code follows project style guidelines
- [ ] Integration tested with MapView rendering

## Dependencies
- **Epic 1**: Field data structures must be complete
- Haven's MapView rendering system
- OpenGL/JOGL rendering pipeline

## Blocks
- Epic 3 (UI Windows) can start in parallel
- Epic 4 (Tile Operations) needs overlay for debugging

## Technical Debt
- May need optimization for very large fields (5000+ tiles)
- Quality text rendering may need font caching for performance
- Consider WebGL-based external viewer (out of scope, future enhancement)

## Testing Strategy
1. Unit tests for coordinate transformations
2. Visual tests for rendering accuracy
3. Performance profiling with large field counts
4. Integration tests with MapView camera movement
5. Manual testing in various game scenarios
