# Overlay Rendering Design

## Overview
This document describes the MapView rendering system and how the farming overlay integrates with it.

## MapView Rendering Architecture

### Rendering Pipeline
MapView handles the 3D game world rendering in several phases:
1. **Terrain rendering** - Ground tiles and height map
2. **Gob rendering** - Game objects (trees, animals, buildings, players)
3. **Custom overlays** - Minesweeper, farming overlay, etc.
4. **UI widgets** - Inventory, menus, etc. (separate from MapView)

### Coordinate Systems

#### 1. Grid Coordinates (Tiles)
- Integer tile coordinates (e.g., `100, 50`)
- Used by farming system for field definitions
- 1 tile = 11 world units (`MCache.tilesz = 11`)

#### 2. World Coordinates (2D)
- Float coordinates in the game world (e.g., `1100.0, 550.0`)
- Grid coords * 11 = World coords
- Used for positioning objects before 3D projection

#### 3. 3D Coordinates (Coord3f)
- World X, Y + Z height from terrain
- Z height obtained via `MCache.getzp(Coord2d)` which interpolates terrain elevation
- Creates true 3D position for camera projection

#### 4. Screen Coordinates
- Final 2D pixel coordinates on screen (e.g., `640, 480`)
- Obtained via `MapView.screenxf(Coord3f)` which applies camera transformation
- Accounts for camera position, rotation, zoom

### Coordinate Conversion Flow
```
Grid (100, 50) 
  → World (1100.0, 550.0)
  → 3D (1100.0, 550.0, terrainZ)
  → Screen (640, 480)
```

## Integration Point: draw() Method

The farming overlay hooks into `MapView.draw(GOut g)` at line ~1000:

```java
public void draw(GOut g) {
    // ... terrain rendering ...
    // ... gob rendering ...
    
    // Custom overlay rendering
    if (CFG.SHOW_MINESWEEPER_OVERLAY.get()) {
        MineSweeper.overlay(g, this);
    }
    
    // Farming overlay integration
    FarmingOverlay.getInstance().render(g, this);
    
    // ... UI elements ...
}
```

This ensures overlays render **after terrain and gobs** but **before UI widgets**.

## Terrain Height Integration

### Why Terrain Height Matters
Fields should follow ground contours, not float at player height. We use `MCache.getzp()` which:
1. Calls `getcz()` internally to interpolate between tile corner heights
2. Returns a `Coord3f` with accurate terrain Z value
3. More accurate than raw `getz(SurfaceID, Coord2d)` for rendering

### Implementation
```java
// Get terrain height for a point
Coord2d worldPoint = new Coord2d(x, y);
Coord3f point3d = mapView.glob.map.getzp(worldPoint);
Coord3f screenPoint = mapView.screenxf(point3d);
```

## Graphics Output (GOut)

### Available Drawing Methods
- `g.line(Coord a, Coord b, int width)` - Draw line between two screen points
- `g.chcolor(Color c)` - Set draw color (with alpha for transparency)
- `g.atext(String text, Coord pos, double ax, double ay)` - Draw aligned text
- `g.image(Tex tex, Coord pos)` - Draw texture/image

### Color Management
```java
g.chcolor(new Color(0, 255, 0, 128)); // Green with 50% opacity
g.line(p1, p2, 2);
g.chcolor(); // Reset to default color
```

## Performance Considerations

### Best Practices
1. **Early exit** - Return immediately if overlay disabled or no fields
2. **Exception handling** - Catch `Loading` exceptions for unloaded map data
3. **Null checks** - `screenxf()` returns null if point not in view frustum
4. **Minimal calculations** - Cache field boundaries, only recalculate on field changes
5. **Culling** - Only render fields visible on screen (future optimization)

### Current Optimizations
- Singleton pattern for overlay manager (no repeated instantiation)
- Only iterate enabled fields
- Graceful degradation on Loading exceptions

## Rendering Shapes

### Rectangles
Must convert all 4 corners individually to account for terrain slope and camera rotation:
```java
Coord3f tl3d = mapView.glob.map.getzp(topLeft);
Coord3f tr3d = mapView.glob.map.getzp(topRight);
Coord3f bl3d = mapView.glob.map.getzp(bottomLeft);
Coord3f br3d = mapView.glob.map.getzp(bottomRight);

Coord3f tlScreen = mapView.screenxf(tl3d);
// ... convert other corners ...

g.line(tlScreen.round2(), trScreen.round2(), 2); // Top edge
// ... draw other 3 edges ...
```

### Circles
Approximate as polygon with 32 segments:
```java
int segments = 32;
for (int i = 0; i < segments; i++) {
    double angle = (2 * Math.PI * i) / segments;
    double x = centerX + radius * Math.cos(angle);
    double y = centerY + radius * Math.sin(angle);
    
    Coord3f point3d = mapView.glob.map.getzp(new Coord2d(x, y));
    points[i] = mapView.screenxf(point3d).round2();
}

// Connect points with lines
for (int i = 0; i < segments; i++) {
    int next = (i + 1) % segments;
    g.line(points[i], points[next], 2);
}
```

## Reference Implementations

### MineSweeper Overlay
- Location: `src/me/ender/minimap/MineSweeper.java`
- Renders grid overlays for mine detection
- Example of working with MapView rendering

### FarmingOverlay
- Location: `src/auto/farming/ui/overlay/FarmingOverlay.java`
- Singleton pattern for global access
- Renders field boundaries with terrain-following shapes
- Integrates with FarmingManager for field data

## Common Pitfalls

### ❌ Don't Use Screen-Space Primitives
```java
// WRONG - Rectangle will be screen-aligned, not world-aligned
g.rect(topLeft, size); 
```

### ✅ Do Transform Individual Points
```java
// CORRECT - Each point projected separately
for each corner:
    Coord3f screen = mapView.screenxf(worldPoint3d);
    // Draw lines connecting screen points
```

### ❌ Don't Use Player Height
```java
// WRONG - Uses player's Z coordinate
Coord3f screen = mapView.screenxf(new Coord2d(x, y));
```

### ✅ Do Use Terrain Height
```java
// CORRECT - Uses terrain elevation
Coord3f point3d = mapView.glob.map.getzp(new Coord2d(x, y));
Coord3f screen = mapView.screenxf(point3d);
```

## Future Enhancements
- Camera frustum culling to skip off-screen fields
- Level-of-detail rendering (simpler shapes when zoomed out)
- Batched rendering for multiple fields
- Cached screen coordinates (invalidate on camera move)
