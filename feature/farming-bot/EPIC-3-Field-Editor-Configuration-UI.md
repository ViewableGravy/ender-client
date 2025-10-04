# Epic 3: Field Editor & Configuration UI

**Epic ID**: FB-EPIC-3  
**Status**: Not Started  
**Priority**: P0 (Blocker)  
**Estimate**: 2 weeks  
**Dependencies**: Epic 1 (Field Data Structures), Epic 2 (Overlay System)  

## Epic Description
Create comprehensive UI for field management, including a field editor for drawing/defining fields on the map, a configuration window for field settings, and integration with the xTended menu system. Users should be able to create, edit, delete, and configure fields entirely through the UI without manual JSON editing.

## Goals
- Implement field editor with drawing tools (rectangle, circle)
- Create farming configuration window with field list
- Support drag-and-drop field creation on map
- Enable field subdivision configuration
- Provide chest linking interface
- Integrate with xTended menu
- Save/load configurations automatically

## User Stories

---

### Story FB-3.1: Create Farming Configuration Window
**Story Points**: 13  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `FarmingConfigWindow.java` extending `Window` class
- [ ] Implement tabbed interface with sections:
  - Fields tab (field list and management)
  - Crop Settings tab (crop configurations)
  - Storage tab (chest management)
  - Overlay Settings tab (from Epic 2)
  - Bot Settings tab (automation settings)
- [ ] Add window title "Farming Configuration"
- [ ] Set default size: 600x800 pixels
- [ ] Make window draggable and resizable
- [ ] Persist window position/size in config
- [ ] Add close button with save confirmation if changes pending
- [ ] Integrate with Widget system for proper lifecycle

#### Technical Notes
```java
package auto.farming.ui;

public class FarmingConfigWindow extends Window {
    private static final Coord DEFAULT_SIZE = new Coord(600, 800);
    private final FarmingManager manager;
    
    public FarmingConfigWindow() {
        super(DEFAULT_SIZE, "Farming Configuration");
        // Create tabbed panel
        // Initialize tabs
    }
}
```

#### Files to Create
- `src/auto/farming/ui/FarmingConfigWindow.java`

---

### Story FB-3.2: Implement Fields Tab with Field List
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create scrollable list widget showing all fields
- [ ] Display for each field:
  - Field name
  - Type (Quality/Quantity) with colored icon
  - Shape (Rectangle/Circle)
  - Crop type with icon
  - Enabled/disabled checkbox
  - Area (tile count)
- [ ] Add action buttons:
  - "Add Field" - opens field editor
  - "Edit Field" - opens field editor for selected field
  - "Delete Field" - confirms and removes field
  - "Duplicate Field" - creates copy of selected field
- [ ] Implement search/filter by name or crop type
- [ ] Add sorting options (by name, type, creation date, area)
- [ ] Show field statistics: total fields, enabled count, total area
- [ ] Double-click field to zoom map to field location

#### Technical Notes
- Use existing list widget components from Haven (see `Inventory.java` patterns)
- Field list updates reactively when fields added/removed
- Icons loaded from resource system
- Zoom functionality interfaces with MapView camera

#### Files to Create
- `src/auto/farming/ui/FieldsTab.java`
- `src/auto/farming/ui/FieldListWidget.java`
- `src/auto/farming/ui/FieldListItem.java`

---

### Story FB-3.3: Create Field Editor Mode
**Story Points**: 34  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create `FieldEditor.java` class for interactive field creation
- [ ] Implement editor activation/deactivation
- [ ] Add editor mode UI overlay with instructions
- [ ] Support two drawing modes:
  - Rectangle: Click-drag to define corners
  - Circle: Click center, drag to define radius
- [ ] Show live preview of field boundaries while drawing
- [ ] Snap to grid (11x11 tile alignment)
- [ ] Display area calculation in real-time
- [ ] Add edit mode for existing fields:
  - Move field (drag entire field)
  - Resize field (drag corner/edge handles)
  - Rotate field (future enhancement)
- [ ] Add cancel (ESC key) and confirm (Enter key) controls
- [ ] Prevent overlapping fields (validation)
- [ ] Show error messages for invalid placements

#### Technical Notes
- Editor mode should suppress normal map interactions (movement, clicking objects)
- Use mouse event handlers in MapView
- Preview rendering uses overlay system from Epic 2
- Grid snapping: round coordinates to nearest 11-unit boundary
- Reference existing editor patterns (e.g., area selection in survey system)

#### Files to Create
- `src/auto/farming/ui/FieldEditor.java`
- `src/auto/farming/ui/EditorMode.java` (enum: RECTANGLE, CIRCLE, EDIT, OFF)
- `src/auto/farming/ui/EditorOverlay.java`

---

### Story FB-3.4: Implement Field Properties Panel
**Story Points**: 21  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Create properties panel shown when field selected or being created
- [ ] Add input fields:
  - Field name (text input)
  - Field type (dropdown: Quality/Quantity)
  - Shape (dropdown: Rectangle/Circle) - disabled if editing existing
  - Crop type (dropdown with icons, searchable)
  - Enabled (checkbox)
- [ ] Add subdivision configuration:
  - Enable subdivisions (checkbox)
  - Subdivision count (number input or preset buttons: 2, 4, 5, 8)
  - Preview subdivision layout
- [ ] Show field statistics:
  - Total area (tiles)
  - Estimated planting time
  - Estimated harvest yield
- [ ] Add validation with error messages
- [ ] Auto-generate field name if empty (e.g., "Wheat Field 1")
- [ ] Save button applies changes

#### Technical Notes
- Crop type dropdown populated from `DefaultCropConfigs.java`
- Subdivision preview shows visual representation
- Field name validation: unique, non-empty, max 50 chars
- Statistics calculations may need crop-specific data

#### Files to Create
- `src/auto/farming/ui/FieldPropertiesPanel.java`
- `src/auto/farming/ui/widgets/CropSelector.java`
- `src/auto/farming/ui/widgets/SubdivisionConfigurator.java`

---

### Story FB-3.5: Create Crop Settings Tab
**Story Points**: 13  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Display list of all crop types in game
- [ ] Show default configurations for each crop:
  - Display name
  - Resource name
  - Growth stages
  - Watering required
  - Default quality thresholds
- [ ] Allow customizing crop configs:
  - Min/max quality for quantity fields
  - Min/max quality for quality fields
  - Custom growth times (if supported)
- [ ] Add "Reset to Defaults" button per crop
- [ ] Provide import/export crop configs (JSON)
- [ ] Show crop icons/thumbnails

#### Technical Notes
- Crop list may need to be discovered from game resources
- Custom configs override defaults from `DefaultCropConfigs.java`
- Import/export useful for sharing configs between characters

#### Files to Create
- `src/auto/farming/ui/CropSettingsTab.java`
- `src/auto/farming/ui/CropConfigEditor.java`

---

### Story FB-3.6: Implement Storage Tab and Chest Linking
**Story Points**: 21  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Create storage management interface
- [ ] Add "Link Chest" mode:
  - Activate mode, click chest in game to link
  - Display chest coordinates and type
  - Assign chest role: Input (seeds), Output (harvest), Both
- [ ] Show list of linked chests with:
  - Coordinates
  - Role (Input/Output/Both)
  - Associated fields
  - Distance from field
  - Remove button
- [ ] Support assigning chests to specific fields
- [ ] Add quality sorting rules for output chests:
  - High quality threshold (e.g., Q > 15 → Chest A)
  - Low quality threshold (e.g., Q < 10 → Chest B)
  - Default chest for everything else
- [ ] Validate chest accessibility (pathfinding check)
- [ ] Highlight linked chests on map overlay

#### Technical Notes
- Chest linking uses Gob detection (click on chest Gob)
- Store chest Gob ID and coordinates (Gob may despawn/respawn)
- Pathfinding validation: ensure chest reachable from field
- Chest type detection: differentiate chest, cupboard, crate, trough

#### Files to Create
- `src/auto/farming/ui/StorageTab.java`
- `src/auto/farming/ui/ChestLinkingMode.java`
- `src/auto/farming/ui/ChestListWidget.java`
- `src/auto/farming/ui/QualitySortingPanel.java`

---

### Story FB-3.7: Add xTended Menu Actions
**Story Points**: 5  
**Priority**: P0  

#### Acceptance Criteria
- [ ] Add actions to `Action.java`:
  - `OPEN_FARMING_CONFIG` - Opens farming config window
  - `TOGGLE_FIELD_EDITOR` - Activates/deactivates field editor
  - `START_FARMING_BOT` - Starts autonomous farming
  - `STOP_FARMING_BOT` - Stops farming bot
  - `PAUSE_FARMING_BOT` - Pauses/resumes farming bot
- [ ] Register actions in `MenuGrid.initCustomPaginae()` under `paginae/add/farming/`
- [ ] Create action icons (PNG images)
- [ ] Add state indicators:
  - Field editor ON/OFF
  - Bot RUNNING/STOPPED/PAUSED
- [ ] Add keyboard shortcuts:
  - Ctrl+Shift+F: Open config window
  - Ctrl+Shift+E: Toggle field editor
  - Ctrl+Shift+S: Start/stop bot

#### Technical Notes
```java
// In Action.java
OPEN_FARMING_CONFIG(gui -> {
    FarmingConfigWindow wnd = gui.getWindow(FarmingConfigWindow.class);
    if (wnd == null) {
        gui.add(new FarmingConfigWindow(), new Coord(100, 100));
    } else {
        wnd.show();
        wnd.raise();
    }
}, "Farming Config", "Open farming configuration window"),

START_FARMING_BOT(gui -> {
    FarmingManager.getInstance().startBot();
}, "Start Farming", "Start autonomous farming bot")
```

#### Files to Modify
- `src/haven/Action.java`
- `src/haven/MenuGrid.java`

#### Files to Create
- `resources/src/local/paginae/add/farming/config.png`
- `resources/src/local/paginae/add/farming/editor.png`
- `resources/src/local/paginae/add/farming/start.png`
- `resources/src/local/paginae/add/farming/stop.png`
- `resources/src/local/paginae/add/farming/pause.png`

---

### Story FB-3.8: Implement Auto-Save and Change Tracking
**Story Points**: 8  
**Priority**: P1  

#### Acceptance Criteria
- [ ] Track unsaved changes in config window
- [ ] Show "*" indicator in window title when changes pending
- [ ] Add "Save" and "Revert" buttons
- [ ] Implement auto-save on field changes (debounced, 5 second delay)
- [ ] Prompt user to save on window close if changes pending
- [ ] Add "Save All" action in xTended menu
- [ ] Show save status indicator (e.g., "Saved" / "Saving..." / "Unsaved changes")
- [ ] Handle save failures gracefully with error messages

#### Technical Notes
- Use dirty flag pattern to track changes
- Debounce auto-save to avoid excessive file I/O
- Save failures may occur due to file permissions or disk full

#### Files to Create
- `src/auto/farming/ui/ChangeTracker.java`
- `src/auto/farming/config/AutoSaveManager.java`

---

### Story FB-3.9: Add Field Templates and Presets
**Story Points**: 13  
**Priority**: P2  

#### Acceptance Criteria
- [ ] Create field template system
- [ ] Add preset templates:
  - "Small Wheat Field" (10x10 rectangle)
  - "Large Carrot Field" (20x20 rectangle)
  - "Beehive Circle" (radius 15, 4 quadrants)
  - "Mixed Crop Circle" (radius 20, 8 segments)
- [ ] Support saving custom templates
- [ ] Add "Create from Template" button in field list
- [ ] Templates include: shape, size, subdivisions, default crop
- [ ] Import/export template library (JSON)
- [ ] Show template preview before creation

#### Technical Notes
- Templates stored in `etc/field-templates.json5`
- User can customize templates and share with others
- Useful for quickly setting up standard farm layouts

#### Files to Create
- `src/auto/farming/config/FieldTemplate.java`
- `src/auto/farming/ui/TemplateSelector.java`
- `etc/field-templates.json5`

---

### Story FB-3.10: Create UI Integration Tests
**Story Points**: 13  
**Priority**: P2  

#### Acceptance Criteria
- [ ] Test field creation through UI
- [ ] Test field editing and deletion
- [ ] Test crop configuration changes
- [ ] Test chest linking workflow
- [ ] Test save/load functionality
- [ ] Test validation and error handling
- [ ] Test window state persistence
- [ ] Document manual testing checklist

#### Technical Notes
- UI testing may require manual validation
- Create test scenarios for all user workflows
- Test with various screen resolutions

#### Files to Create
- `test/auto/farming/ui/FarmingConfigWindowTest.java`
- `feature/farming-bot/docs/UI-TESTING-GUIDE.md`

---

## Definition of Done
- [ ] All stories completed and code reviewed
- [ ] Users can create, edit, delete fields through UI
- [ ] Field editor works smoothly with visual feedback
- [ ] Configuration persists correctly across restarts
- [ ] xTended menu actions functional with state indicators
- [ ] No UI bugs or usability issues
- [ ] Code follows project style guidelines
- [ ] User documentation created

## Dependencies
- **Epic 1**: Field data structures
- **Epic 2**: Overlay system for field preview
- Haven's Widget/Window system
- xTended menu framework

## Blocks
- Epic 4 (Tile Operations) can proceed independently
- Epic 5 & 6 (Bot Logic) need field configuration to be functional

## Technical Debt
- Field editor may need refinement based on user feedback
- Template system could be expanded with more advanced features
- Consider adding undo/redo for field editing (future enhancement)

## User Documentation
- Create user guide for field setup
- Document keyboard shortcuts
- Provide video tutorial (optional)
- Add tooltips for all UI elements
