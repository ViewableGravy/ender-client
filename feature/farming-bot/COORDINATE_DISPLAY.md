# Coordinate Display Implementation

## Summary
Added a coordinate display widget to help with testing the farming bot feature. The display shows both world coordinates and grid coordinates (tile coordinates) for the player's current position.

## Changes Made

### 1. New File: `src/haven/CoordinateDisplay.java`
Created a new widget that displays player coordinates in real-time:
- **World Coordinates**: Raw x,y position in the game world
- **Grid Coordinates**: Tile-based coordinates (world coords / 11)
- Auto-updates every frame (via `tick()` method)
- Semi-transparent black background with white text
- Size: 250x50 pixels

**Key Implementation Details**:
```java
// Get player position from MapView
Gob player = mapView.player();
Coord3f playerPos = player.getc();

// Calculate grid coordinates
double gridX = worldX / MCache.tilesz.x; // 11.0
double gridY = playerPos.y / MCache.tilesz.y;
```

### 2. Modified: `src/haven/GameUI.java`
**Added field** (line ~64):
```java
public CoordinateDisplay coordDisplay;
```

**Added initialization** (line ~1073, in `addchild()` method):
```java
// Add coordinate display for testing farming bot
if (coordDisplay != null) {
    ui.destroy(coordDisplay);
}
coordDisplay = add(new CoordinateDisplay(map), new Coord(UI.scale(10), UI.scale(100)));
```

The coordinate display is created when the MapView is added (server message "mapview"), ensuring the map reference is valid.

## Usage for Testing
When you run the game, the coordinate display will appear in the upper-left corner showing:
```
World: (1234.5, -567.8) | Grid: (112.2, 51.6)
```

Use the **Grid coordinates** when creating test fields with `FarmingTestUtils.createTestField(x, y, name)`. The grid coordinates are what the farming system uses internally.

## Position
- Located at (10, 100) scaled pixels from top-left
- Below the portrait panel
- Semi-transparent so it doesn't block too much view
- Always visible when in-game

## Future Enhancements
Could be made draggable or toggleable via menu action if desired, but for testing purposes this fixed position should be sufficient.

## Testing
Once the build completes:
1. Launch the client: `java -jar bin/hafen.jar`
2. Log into the game
3. The coordinate display will appear in the upper-left
4. Move around and watch the coordinates update
5. Use the grid coordinates to create test fields at specific locations

## Build Notes
The build system requires:
- JDK 8 (not JRE) - now installed via scoop: `openjdk8-redhat`
- Apache Ant - installed via scoop
- JAVA_HOME must point to JDK directory
