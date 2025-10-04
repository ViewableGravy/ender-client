# Farming Bot - Testing Guide (Epic 2 Milestone)

## What's Been Implemented

**Epic 1 - Field Data Structures** (Complete - 93 SP)
- Full data model for farm fields, crops, storage, and tile tracking
- JSON persistence to `~/.haven/farming.json5`
- Field manager singleton with CRUD operations

**Epic 2 - Overlay System** (Partial - 13 SP)
- Visual field boundary rendering on the map
- xTended menu toggle action
- Support for rectangle and circle fields with color coding

## How to Test

### 1. Build and Run the Client

```bash
# Build the client
ant

# Run the client
ant run
# OR
java -jar bin/hafen.jar
```

### 2. Enable the Farming Overlay

1. Log into the game
2. Click the **xTended menu** icon (folder icon in menu grid)
3. Look for **"Toggle Farming Overlay"** action
4. Click it to enable (icon should show as "ON")

### 3. Create Test Fields

You can create test fields using the developer console or by calling methods directly:

#### Option A: Using Console Commands

Open the Java console (if available) and run:

```java
// Create a 10x10 rectangular field at grid coordinates (100, 100)
auto.farming.util.FarmingTestUtils.createTestField(100, 100, "North Wheat Field");

// Create a circular field with radius 7 tiles
auto.farming.util.FarmingTestUtils.createTestCircleField(150, 150, 7.0, "Quality Flax");

// List all fields
auto.farming.util.FarmingTestUtils.printFields();

// Clear all fields
auto.farming.util.FarmingTestUtils.clearAllFields();
```

#### Option B: Manual Field Creation via Code

Add this code temporarily to `GameUI.java` constructor (after FarmingManager init):

```java
// TESTING: Create sample fields
auto.farming.util.FarmingTestUtils.createTestField(100, 100, "Test Farm 1");
auto.farming.util.FarmingTestUtils.createTestCircleField(150, 150, 8.0, "Test Circle");
```

### 4. What You Should See

When the overlay is enabled and you have fields created:

- **Rectangle fields**: Green/gold outlined rectangles on the map
  - **Green (Lime)**: QUANTITY production fields
  - **Gold**: QUALITY breeding fields
- **Circle fields**: Filled circles with semi-transparent overlay
- **Field names**: Displayed at the center of each field
- **Persistent**: Fields are saved to `~/.haven/farming.json5` and will reload on next login

### 5. Finding Your Coordinates

To find where to create fields:
1. Position your character where you want the field center
2. Check your coordinates (typically shown in game UI or console)
3. Divide by 11 to convert from world coords to grid tiles
4. Use those grid coordinates in `createTestField(x, y, name)`

Example: If you're at world coordinates (1100, 2200):
- Grid X = 1100 / 11 = 100
- Grid Y = 2200 / 11 = 200
- Create field: `createTestField(100, 200, "My Field")`

## Configuration File Location

Fields are saved to: `~/.haven/farming.json5`

On Windows: `C:\Users\<YourUsername>\.haven\farming.json5`

You can manually edit this file to:
- Change field names
- Adjust boundaries
- Enable/disable fields
- Add storage chest configurations

## Known Limitations (Current Milestone)

- ✅ Field boundaries render correctly
- ✅ Toggle on/off works
- ✅ Persistent configuration saves/loads
- ❌ No UI to create/edit fields (must use console)
- ❌ No tile state visualization (growing crops, etc.)
- ❌ No subdivision rendering
- ❌ No performance optimization (may lag with 100+ fields)

## Next Steps

To make this fully usable, the following features are needed:
- **Field creation UI**: Right-click menu or dialog to define fields
- **Field editing**: Drag corners to resize, click to rename
- **Tile state overlay**: Show which tiles are planted, growing, harvestable
- **Performance optimization**: Only render visible fields, use cached geometry

## Troubleshooting

**Overlay doesn't show:**
- Make sure you clicked the "Toggle Farming Overlay" action
- Check that FarmingManager.getInstance().isEnabled() returns true
- Verify fields were actually created (use printFields())

**Fields don't save:**
- Check console for "FarmingManager initialized" message
- Verify `~/.haven/farming.json5` exists and is writable
- Look for error messages in console output

**Can't see fields on map:**
- Fields use grid coordinates, not world coordinates
- Make sure you're looking at the right area
- Try creating a field at your current position first
- Check that field is enabled: field.isEnabled() should be true

## Example Field Configuration

Here's what a saved field looks like in `farming.json5`:

```json
{
  "version": 1,
  "fields": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "North Wheat Field",
      "type": "QUANTITY",
      "shape": "RECTANGLE",
      "grid": {
        "shape": "RECTANGLE",
        "bounds": [95.0, 95.0, 105.0, 105.0]
      },
      "enabled": true,
      "createdAt": 1696435200000
    }
  ],
  "storage": {},
  "crops": []
}
```

## Testing Checklist

- [ ] Build completes without errors
- [ ] Client launches successfully
- [ ] FarmingManager initializes (check console)
- [ ] xTended menu shows farming overlay toggle
- [ ] Toggle action changes state (ON/OFF indicator)
- [ ] Test field creation works
- [ ] Fields appear on map when overlay enabled
- [ ] Fields disappear when overlay disabled
- [ ] Fields persist after logout/login
- [ ] Rectangle fields render correctly
- [ ] Circle fields render correctly
- [ ] Field names display at center
- [ ] Color coding works (gold vs green)
