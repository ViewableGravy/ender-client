package auto.farming.ui.overlay;

import auto.farming.FarmingManager;
import auto.farming.model.*;
import haven.*;

import java.awt.*;

/**
 * Main overlay renderer for the farming bot.
 * Draws field boundaries, subdivisions, and tile states on the MapView.
 */
public class FarmingOverlay {
    private static final FarmingOverlay INSTANCE = new FarmingOverlay();
    
    private boolean enabled = false;
    private final FarmingManager manager;
    
    // Visual settings
    private static final Color RECTANGLE_COLOR = new Color(0, 255, 0, 128); // Green with alpha
    private static final Color CIRCLE_COLOR = new Color(0, 200, 255, 128); // Cyan with alpha
    private static final float LINE_WIDTH = 2.0f;
    
    /**
     * Private constructor for singleton pattern.
     */
    private FarmingOverlay() {
        this.manager = FarmingManager.getInstance();
    }
    
    /**
     * Gets the singleton instance.
     * @return The farming overlay instance
     */
    public static FarmingOverlay getInstance() {
        return INSTANCE;
    }
    
    /**
     * Toggles the overlay on/off.
     */
    public void toggle() {
        enabled = !enabled;
        System.out.println("Farming overlay " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Sets whether the overlay is enabled.
     * @param enabled true to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Checks if the overlay is enabled.
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Main rendering method called from MapView.
     * @param g Graphics output
     * @param mapView The map view being drawn
     */
    public void render(GOut g, MapView mapView) {
        if (!enabled || !manager.isInitialized()) {
            return;
        }
        
        try {
            // Render all enabled fields
            for (FarmField field : manager.getEnabledFields()) {
                renderField(g, mapView, field);
            }
        } catch (Exception e) {
            // Catch any rendering errors to avoid crashing the client
            System.err.println("Error rendering farming overlay: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Renders a single field on the map.
     * @param g Graphics output
     * @param mapView The map view
     * @param field The field to render
     */
    private void renderField(GOut g, MapView mapView, FarmField field) {
        FieldGrid grid = field.getGrid();
        if (grid == null) {
            return;
        }
        
        // Get field color based on type
        Color fieldColor = getFieldColor(field);
        
        // Render based on shape
        if (field.getShape() == FieldShape.RECTANGLE) {
            renderRectangleField(g, mapView, grid, fieldColor, field);
        } else if (field.getShape() == FieldShape.CIRCLE) {
            renderCircleField(g, mapView, grid, fieldColor, field);
        }
    }
    
    /**
     * Renders a rectangular field.
     */
    private void renderRectangleField(GOut g, MapView mapView, FieldGrid grid, Color color, FarmField field) {
        double[] bounds = grid.getBounds();
        
        // Convert grid coordinates to world coordinates
        // Grid coords are in tiles (1 tile = 11 world units)
        double minX = bounds[0] * 11.0;
        double minY = bounds[1] * 11.0;
        double maxX = bounds[2] * 11.0;
        double maxY = bounds[3] * 11.0;
        
        // Get all 4 corners in world space
        Coord2d topLeft = new Coord2d(minX, minY);
        Coord2d topRight = new Coord2d(maxX, minY);
        Coord2d bottomLeft = new Coord2d(minX, maxY);
        Coord2d bottomRight = new Coord2d(maxX, maxY);
        
        // Get terrain height for each corner and convert to 3D coordinates
        try {
            Coord3f tl3d = mapView.glob.map.getzp(topLeft);
            Coord3f tr3d = mapView.glob.map.getzp(topRight);
            Coord3f bl3d = mapView.glob.map.getzp(bottomLeft);
            Coord3f br3d = mapView.glob.map.getzp(bottomRight);
            
            // Convert all corners to screen space
            Coord3f tlScreen = mapView.screenxf(tl3d);
            Coord3f trScreen = mapView.screenxf(tr3d);
            Coord3f blScreen = mapView.screenxf(bl3d);
            Coord3f brScreen = mapView.screenxf(br3d);
            
            if (tlScreen == null || trScreen == null || blScreen == null || brScreen == null) {
                return; // Map not loaded yet
            }
            
            Coord tl = tlScreen.round2();
            Coord tr = trScreen.round2();
            Coord bl = blScreen.round2();
            Coord br = brScreen.round2();
            
            // Draw rectangle as 4 lines connecting the corners
            g.chcolor(color);
            g.line(tl, tr, 2); // Top edge
            g.line(tr, br, 2); // Right edge
            g.line(br, bl, 2); // Bottom edge
            g.line(bl, tl, 2); // Left edge
            
            // Draw field name at center with terrain height
            Coord2d centerGrid = grid.getCenter();
            Coord2d centerWorld = new Coord2d(centerGrid.x * 11.0, centerGrid.y * 11.0);
            Coord3f center3d = mapView.glob.map.getzp(centerWorld);
            Coord3f centerScreen = mapView.screenxf(center3d);
            
            if (centerScreen != null) {
                g.chcolor(Color.WHITE);
                g.atext(field.getName(), centerScreen.round2(), 0.5, 0.5);
            }
            
            g.chcolor();
        } catch (Loading e) {
            // Map data not loaded yet
        }
    }
    
    /**
     * Renders a circular field.
     * Draws the circle as a polygon with world-space points projected to screen space.
     */
    private void renderCircleField(GOut g, MapView mapView, FieldGrid grid, Color color, FarmField field) {
        Coord2d centerGrid = grid.getCenter();
        double radius = grid.getRadius();
        
        // Convert to world coordinates
        double centerWorldX = centerGrid.x * 11.0;
        double centerWorldY = centerGrid.y * 11.0;
        double radiusWorld = radius * 11.0;
        
        // Draw circle as a polygon with multiple points
        int segments = 32; // Number of line segments to approximate the circle
        Coord[] points = new Coord[segments];
        
        try {
            for (int i = 0; i < segments; i++) {
                double angle = (2 * Math.PI * i) / segments;
                double x = centerWorldX + radiusWorld * Math.cos(angle);
                double y = centerWorldY + radiusWorld * Math.sin(angle);
                
                // Get terrain height at this point
                Coord2d worldPoint = new Coord2d(x, y);
                Coord3f point3d = mapView.glob.map.getzp(worldPoint);
                Coord3f screenPoint = mapView.screenxf(point3d);
                
                if (screenPoint == null) {
                    return; // Map not loaded yet
                }
                
                points[i] = screenPoint.round2();
            }
            
            // Draw lines connecting all points
            g.chcolor(color);
            for (int i = 0; i < segments; i++) {
                int next = (i + 1) % segments;
                g.line(points[i], points[next], 2);
            }
            
            // Draw field name at center
            Coord2d centerWorld = new Coord2d(centerWorldX, centerWorldY);
            Coord3f center3d = mapView.glob.map.getzp(centerWorld);
            Coord3f centerScreen3f = mapView.screenxf(center3d);
            
            if (centerScreen3f != null) {
                g.chcolor(Color.WHITE);
                g.atext(field.getName(), centerScreen3f.round2(), 0.5, 0.5);
            }
            
            g.chcolor();
        } catch (Loading e) {
            // Map data not loaded yet
        }
    }
    
    /**
     * Gets the rendering color for a field based on its type.
     */
    private Color getFieldColor(FarmField field) {
        if (field.getType() == FieldType.QUALITY) {
            return new Color(255, 215, 0, 100); // Gold for quality breeding
        } else {
            return new Color(50, 205, 50, 100); // Lime green for quantity production
        }
    }
}
