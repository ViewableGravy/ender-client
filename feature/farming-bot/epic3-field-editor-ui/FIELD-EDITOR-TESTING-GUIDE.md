# FB-3.3 Field Editor Mode - Testing Guide

## Quick Start

1. **Launch game**: `ant run` or `java -jar bin\hafen.jar`
2. **Open config**: Type `:farmconfig` in game console
3. **Add field**: Click "Add Field" button

## Test Cases

### Test 1: Rectangle Field Creation

1. Click "Add Field" button
2. Select "Rectangle" from dialog
3. Click first corner on map (field editor activates)
4. Move mouse - see yellow preview rectangle
5. Click second corner
6. ✅ Field created, added to list, config saved

**Expected:**
- Grid snapping (coordinates rounded to 11-unit boundaries)
- Area display in real-time (e.g., "Area: 16 tiles (4x4)")
- Yellow preview overlay
- Instruction text: "Click first corner..." then "Click opposite corner | ESC to cancel"

### Test 2: Circle Field Creation

1. Click "Add Field" button
2. Select "Circle" from dialog
3. Click center point on map
4. Move mouse - see yellow circle preview
5. Click to set radius
6. ✅ Field created, added to list

**Expected:**
- Circle drawn with 64 line segments
- Radius snapped to tile size multiples
- Area display: "Area: X tiles (r=Y)"

### Test 3: ESC Cancel

1. Start creating a field (Rectangle or Circle)
2. Click first point
3. Press ESC key
4. ✅ Editor deactivated, window shown again

**Expected:**
- "Field creation cancelled" message in console
- No field created
- Config window reappears

### Test 4: Overlap Validation

1. Create a field successfully
2. Try creating another field that overlaps
3. ✅ Error displayed: "Field overlaps with existing field: [name]"

**Expected:**
- Field not created
- Error message in console
- Editor remains active (can try again)

### Test 5: Auto-Naming

1. Create multiple rectangle fields
2. ✅ Names: "Rectangle Field 1", "Rectangle Field 2", etc.
3. Create multiple circle fields
4. ✅ Names: "Circle Field 1", "Circle Field 2", etc.

### Test 6: Field List Integration

1. Create 2-3 fields
2. Return to config window
3. ✅ All fields appear in list
4. ✅ Statistics updated (Total, Enabled, Area)
5. ✅ Fields are enabled by default

## Visual Checks

- [x] Yellow preview color
- [x] Grid snap indicators (white squares)
- [x] Instruction text at top of screen
- [x] Area calculation display
- [x] Config window hides during editing
- [x] Config window shows after completion/cancel

## Console Commands

Useful for testing:
- `:farmconfig` - Open configuration window
- `:farmtest <name>` - Create test field at player position
- `:farmtest <gridX> <gridY> <name>` - Create test field at coordinates
- `:farmclear` - Remove all fields

## Known Limitations (FB-3.3 Scope)

- **No field editing yet** (Edit button is placeholder - will be FB-3.4)
- **No crop assignment** - Fields created without crop type (will be FB-3.4)
- **Default to QUALITY type** - Can't choose field type during creation (will be FB-3.4)
- **Simple overlap check** - Bounding box only, not pixel-perfect
- **No undo** - Must delete and recreate if mistake

## Troubleshooting

**Field editor not activating:**
- Check console for errors
- Ensure MapView is not null
- Try closing/reopening config window

**ESC key not working:**
- Check if editor is actually active (instruction text visible)
- Try clicking on map first

**Fields not appearing in list:**
- Click "Refresh" button
- Check console for save errors
- Verify FarmingManager is initialized

## Success Criteria

All acceptance criteria from FB-3.3 story met:
- ✅ EditorMode, EditorOverlay, FieldEditor classes created
- ✅ Activation/deactivation works
- ✅ UI overlay with instructions shown
- ✅ Rectangle and circle drawing modes
- ✅ Live preview rendering
- ✅ Grid snapping (11x11 tiles)
- ✅ Real-time area calculation
- ✅ ESC cancel, Enter not needed (click to confirm)
- ✅ Overlap validation
- ✅ Error messages displayed

## Next Steps (FB-3.4)

Field Properties Panel will add:
- Edit existing fields
- Change field shape/bounds
- Set crop type
- Choose field type (Quality/Quantity)
- Configure subdivision settings
