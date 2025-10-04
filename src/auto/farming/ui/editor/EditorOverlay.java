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
     * Sets the radius for circle mode preview.
     * @param radius The radius in world units
     */
    public void setRadius(double radius) {
        // Snap radius to multiples of tile size
        this.radius = Math.round(radius / TILE_SIZE) * TILE_SIZE;
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
        
        // Draw first corner indicator
        Coord3f sc1_3f = mapView.screenxf(firstCorner);
        if (sc1_3f != null) {
            Coord sc1 = new Coord((int)sc1_3f.x, (int)sc1_3f.y);
            g.chcolor(GRID_SNAP_COLOR);
            g.frect(sc1.sub((int)GRID_SNAP_SIZE / 2, (int)GRID_SNAP_SIZE / 2), 
                    new Coord((int)GRID_SNAP_SIZE, (int)GRID_SNAP_SIZE));
        }
        
        // If we have a second corner, draw the preview rectangle
        if (secondCorner != null) {
            Coord3f sc1_3f2 = mapView.screenxf(firstCorner);
            Coord3f sc2_3f = mapView.screenxf(secondCorner);
            if (sc1_3f2 != null && sc2_3f != null) {
                Coord sc1 = new Coord((int)sc1_3f2.x, (int)sc1_3f2.y);
                Coord sc2 = new Coord((int)sc2_3f.x, (int)sc2_3f.y);
                // Calculate rectangle bounds
                int minX = Math.min(sc1.x, sc2.x);
                int minY = Math.min(sc1.y, sc2.y);
                int width = Math.abs(sc2.x - sc1.x);
                int height = Math.abs(sc2.y - sc1.y);
                
                // Draw preview rectangle
                g.chcolor(PREVIEW_COLOR);
                g.frect(new Coord(minX, minY), new Coord(width, height));
                
                // Draw border
                g.chcolor(new Color(255, 255, 0, 255)); // Solid yellow
                g.rect(new Coord(minX, minY), new Coord(width, height));
                
                // Calculate and display area
                double worldWidth = Math.abs(secondCorner.x - firstCorner.x);
                double worldHeight = Math.abs(secondCorner.y - firstCorner.y);
                int tiles = (int)((worldWidth * worldHeight) / (TILE_SIZE * TILE_SIZE));
                
                String areaText = String.format("Area: %d tiles (%dx%d)", 
                    tiles, 
                    (int)(worldWidth / TILE_SIZE),
                    (int)(worldHeight / TILE_SIZE));
                
                Text areaLabel = Text.render(areaText);
                g.chcolor(Color.WHITE);
                g.image(areaLabel.tex(), new Coord(minX, minY - 20));
                areaLabel.dispose();
            }
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
        
        Coord3f center3f = mapView.screenxf(centerPoint);
        if (center3f == null) {
            return;
        }
        
        Coord center = new Coord((int)center3f.x, (int)center3f.y);
        
        // Draw center indicator
        g.chcolor(GRID_SNAP_COLOR);
        g.frect(center.sub((int)GRID_SNAP_SIZE / 2, (int)GRID_SNAP_SIZE / 2), 
                new Coord((int)GRID_SNAP_SIZE, (int)GRID_SNAP_SIZE));
        
        // If we have a radius, draw the preview circle
        if (radius > 0) {
            // Convert radius to screen space
            Coord2d radiusPoint = centerPoint.add(radius, 0);
            Coord3f radiusScreen3f = mapView.screenxf(radiusPoint);
            
            if (radiusScreen3f != null) {
                Coord radiusScreen = new Coord((int)radiusScreen3f.x, (int)radiusScreen3f.y);
                int screenRadius = (int)center.dist(radiusScreen);
                
                // Draw circle as lines between points
                int segments = 64;
                Coord lastPoint = null;
                
                g.chcolor(new Color(255, 255, 0, 255)); // Solid yellow border
                for (int i = 0; i <= segments; i++) {
                    double angle = (2 * Math.PI * i) / segments;
                    int x = center.x + (int)(screenRadius * Math.cos(angle));
                    int y = center.y + (int)(screenRadius * Math.sin(angle));
                    Coord point = new Coord(x, y);
                    
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
                g.image(areaLabel.tex(), center.sub(areaLabel.sz().x / 2, screenRadius + 20));
                areaLabel.dispose();
            }
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
