package auto.farming.ui.editor;

import auto.farming.model.FarmField;
import auto.farming.model.FieldGrid;
import auto.farming.model.FieldShape;
import auto.farming.ui.overlay.OverlaySettings;
import haven.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Visual overlay for the field editor mode.
 * Shows preview of field being created/edited, instructions, and grid snapping.
 */
public class EditorOverlay {
    private final OverlaySettings settings;
    
    // Grid snapping constant (11x11 tiles)
    public static final double TILE_SIZE = 11.0;
    
    // Preview state
    private Coord2d firstCorner = null;
    private Coord2d secondCorner = null;
    private Coord2d centerPoint = null;
    private double radius = 0.0;
    
    // Visual constants
    private static final Color PREVIEW_COLOR = new Color(255, 255, 0, 100); // Yellow with alpha
    private static final Color GRID_SNAP_COLOR = new Color(255, 255, 255, 150); // White indicators
    private static final float PREVIEW_LINE_WIDTH = 2.5f;
    private static final float GRID_SNAP_SIZE = 8.0f;
    
    /**
     * Creates a new editor overlay.
     * @param settings The overlay settings to use for rendering
     */
    public EditorOverlay(OverlaySettings settings) {
        this.settings = settings;
    }
    
    /**
     * Snaps a coordinate to the nearest grid boundary.
     * @param coord The coordinate to snap
     * @return Snapped coordinate
     */
    public static Coord2d snapToGrid(Coord2d coord) {
        double x = Math.round(coord.x / TILE_SIZE) * TILE_SIZE;
        double y = Math.round(coord.y / TILE_SIZE) * TILE_SIZE;
        return new Coord2d(x, y);
    }
    
    /**
     * Sets the first corner for rectangle mode.
     * @param corner The first corner position (world coordinates)
     */
    public void setFirstCorner(Coord2d corner) {
        this.firstCorner = snapToGrid(corner);
        this.secondCorner = null;
    }
    
    /**
     * Sets the second corner for rectangle mode preview.
     * @param corner The second corner position (world coordinates)
     */
    public void setSecondCorner(Coord2d corner) {
        this.secondCorner = snapToGrid(corner);
    }
    
    /**
     * Sets the center point for circle mode.
     * @param center The center position (world coordinates)
     */
    public void setCenterPoint(Coord2d center) {
        this.centerPoint = snapToGrid(center);
        this.radius = 0.0;
    }
    
    /**
     * Sets the radius for circle mode preview by calculating distance from center.
     * @param mousePos Current mouse position in world coordinates
     */
    public void setRadiusFromMouse(Coord2d mousePos) {
        if (centerPoint == null) {
            return;
        }
        // Calculate distance in world coordinates
        double dx = mousePos.x - centerPoint.x;
        double dy = mousePos.y - centerPoint.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        // Snap radius to multiples of tile size
        this.radius = Math.round(dist / TILE_SIZE) * TILE_SIZE;
    }
    
    /**
     * Clears all preview state.
     */
    public void clear() {
        this.firstCorner = null;
        this.secondCorner = null;
        this.centerPoint = null;
        this.radius = 0.0;
    }
    
    /**
     * Renders the editor overlay for rectangle mode.
     * @param g Graphics output
     * @param mapView The map view
     */
    public void renderRectanglePreview(GOut g, MapView mapView) {
        if (firstCorner == null) {
            return;
        }
        
        try {
            // Get 3D coordinate with terrain height for first corner
            Coord3f corner3d = mapView.glob.map.getzp(firstCorner);
            if (corner3d == null) return;
            
            // Draw first corner indicator
            Coord3f sc1_3f = mapView.screenxf(corner3d);
            if (sc1_3f != null) {
                Coord sc1 = new Coord((int)sc1_3f.x, (int)sc1_3f.y);
                g.chcolor(GRID_SNAP_COLOR);
                g.frect(sc1.sub((int)GRID_SNAP_SIZE / 2, (int)GRID_SNAP_SIZE / 2), 
                        new Coord((int)GRID_SNAP_SIZE, (int)GRID_SNAP_SIZE));
            }
            
            // If we have a second corner, draw the preview rectangle
            if (secondCorner != null) {
                // Get all 4 corners in world space
                double minX = Math.min(firstCorner.x, secondCorner.x);
                double minY = Math.min(firstCorner.y, secondCorner.y);
                double maxX = Math.max(firstCorner.x, secondCorner.x);
                double maxY = Math.max(firstCorner.y, secondCorner.y);
                
                Coord2d topLeft = new Coord2d(minX, minY);
                Coord2d topRight = new Coord2d(maxX, minY);
                Coord2d bottomLeft = new Coord2d(minX, maxY);
                Coord2d bottomRight = new Coord2d(maxX, maxY);
                
                // Get terrain height for each corner
                Coord3f tl3d = mapView.glob.map.getzp(topLeft);
                Coord3f tr3d = mapView.glob.map.getzp(topRight);
                Coord3f bl3d = mapView.glob.map.getzp(bottomLeft);
                Coord3f br3d = mapView.glob.map.getzp(bottomRight);
                
                if (tl3d == null || tr3d == null || bl3d == null || br3d == null) return;
                
                // Convert to screen coordinates
                Coord3f tlScreen = mapView.screenxf(tl3d);
                Coord3f trScreen = mapView.screenxf(tr3d);
                Coord3f blScreen = mapView.screenxf(bl3d);
                Coord3f brScreen = mapView.screenxf(br3d);
                
                if (tlScreen == null || trScreen == null || blScreen == null || brScreen == null) return;
                
                Coord tl = new Coord((int)tlScreen.x, (int)tlScreen.y);
                Coord tr = new Coord((int)trScreen.x, (int)trScreen.y);
                Coord bl = new Coord((int)blScreen.x, (int)blScreen.y);
                Coord br = new Coord((int)brScreen.x, (int)brScreen.y);
                
                // Draw rectangle as 4 lines (terrain-following)
                g.chcolor(new Color(255, 255, 0, 255)); // Solid yellow
                g.line(tl, tr, 2.5);
                g.line(tr, br, 2.5);
                g.line(br, bl, 2.5);
                g.line(bl, tl, 2.5);
                
                // Calculate and display area
                double worldWidth = Math.abs(secondCorner.x - firstCorner.x);
                double worldHeight = Math.abs(secondCorner.y - firstCorner.y);
                int tiles = (int)((worldWidth * worldHeight) / (TILE_SIZE * TILE_SIZE));
                
                String areaText = String.format("Area: %d tiles (%.0fx%.0f)", 
                    tiles, 
                    worldWidth / TILE_SIZE,
                    worldHeight / TILE_SIZE);
                
                Text areaLabel = Text.render(areaText);
                g.chcolor(Color.WHITE);
                g.image(areaLabel.tex(), new Coord(tl.x, tl.y - 20));
                areaLabel.dispose();
            }
        } catch (Exception e) {
            // Ignore rendering errors
        }
        
        g.chcolor();
    }
    
    /**
     * Renders the editor overlay for circle mode.
     * @param g Graphics output
     * @param mapView The map view
     */
    public void renderCirclePreview(GOut g, MapView mapView) {
        if (centerPoint == null) {
            return;
        }
        
        try {
            // Get 3D coordinate with terrain height for center
            Coord3f center3d = mapView.glob.map.getzp(centerPoint);
            if (center3d == null) return;
            
            Coord3f center3f = mapView.screenxf(center3d);
            if (center3f == null) return;
            
            Coord center = new Coord((int)center3f.x, (int)center3f.y);
            
            // Draw center indicator
            g.chcolor(GRID_SNAP_COLOR);
            g.frect(center.sub((int)GRID_SNAP_SIZE / 2, (int)GRID_SNAP_SIZE / 2), 
                    new Coord((int)GRID_SNAP_SIZE, (int)GRID_SNAP_SIZE));
            
            // If we have a radius, draw the preview circle
            if (radius > 0) {
                // Draw circle as lines between points in world space
                int segments = 64;
                Coord lastPoint = null;
                
                g.chcolor(new Color(255, 255, 0, 255)); // Solid yellow border
                for (int i = 0; i <= segments; i++) {
                    double angle = (2 * Math.PI * i) / segments;
                    double worldX = centerPoint.x + radius * Math.cos(angle);
                    double worldY = centerPoint.y + radius * Math.sin(angle);
                    
                    Coord2d worldPoint = new Coord2d(worldX, worldY);
                    Coord3f point3d = mapView.glob.map.getzp(worldPoint);
                    if (point3d == null) continue;
                    
                    Coord3f pointScreen3f = mapView.screenxf(point3d);
                    if (pointScreen3f == null) continue;
                    
                    Coord point = new Coord((int)pointScreen3f.x, (int)pointScreen3f.y);
                    
                    if (lastPoint != null) {
                        g.line(lastPoint, point, 2.0);
                    }
                    lastPoint = point;
                }
                
                // Calculate and display area
                double area = Math.PI * radius * radius;
                int tiles = (int)(area / (TILE_SIZE * TILE_SIZE));
                
                String areaText = String.format("Area: %d tiles (r=%.0f)", tiles, radius / TILE_SIZE);
                
                Text areaLabel = Text.render(areaText);
                g.chcolor(Color.WHITE);
                g.image(areaLabel.tex(), center.sub(areaLabel.sz().x / 2, 20));
                areaLabel.dispose();
            }
        } catch (Exception e) {
            // Ignore rendering errors
        }
        
        g.chcolor();
    }
    
    /**
     * Renders instruction text for the current mode.
     * @param g Graphics output
     * @param mode The current editor mode
     * @param step The current step in the creation process
     */
    public void renderInstructions(GOut g, EditorMode mode, int step) {
        String instruction = "";
        
        switch (mode) {
            case RECTANGLE:
                if (step == 0) {
                    instruction = "Click first corner of rectangle field";
                } else if (step == 1) {
                    instruction = "Click opposite corner | ESC to cancel";
                }
                break;
            case CIRCLE:
                if (step == 0) {
                    instruction = "Click center of circular field";
                } else if (step == 1) {
                    instruction = "Click to set radius | ESC to cancel";
                }
                break;
            case EDIT:
                instruction = "Click field to edit | ESC to cancel";
                break;
            default:
                return;
        }
        
        if (!instruction.isEmpty()) {
            Text instructionText = Text.render(instruction);
            Coord pos = new Coord(10, 10);
            
            // Draw background
            g.chcolor(new Color(0, 0, 0, 180));
            g.frect(pos.sub(5, 5), instructionText.sz().add(10, 10));
            
            // Draw text
            g.chcolor(Color.WHITE);
            g.image(instructionText.tex(), pos);
            g.chcolor();
            
            instructionText.dispose();
        }
    }
    
    /**
     * Gets the current preview field grid for rectangle mode.
     * @return The field grid if valid, null otherwise
     */
    public FieldGrid getRectangleFieldGrid() {
        if (firstCorner == null || secondCorner == null) {
            return null;
        }
        
        double gridX1 = firstCorner.x / TILE_SIZE;
        double gridY1 = firstCorner.y / TILE_SIZE;
        double gridX2 = secondCorner.x / TILE_SIZE;
        double gridY2 = secondCorner.y / TILE_SIZE;
        
        double minX = Math.min(gridX1, gridX2);
        double minY = Math.min(gridY1, gridY2);
        double maxX = Math.max(gridX1, gridX2);
        double maxY = Math.max(gridY1, gridY2);
        
        return FieldGrid.rectangle(minX, minY, maxX, maxY);
    }
    
    /**
     * Gets the current preview field grid for circle mode.
     * @return The field grid if valid, null otherwise
     */
    public FieldGrid getCircleFieldGrid() {
        if (centerPoint == null || radius <= 0) {
            return null;
        }
        
        double centerX = centerPoint.x / TILE_SIZE;
        double centerY = centerPoint.y / TILE_SIZE;
        double radiusTiles = radius / TILE_SIZE;
        
        return FieldGrid.circle(centerX, centerY, radiusTiles);
    }
}
