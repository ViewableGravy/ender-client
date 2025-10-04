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
        
        // Convert grid coordinates to screen coordinates
        Coord2d minWorld = new Coord2d(bounds[0] * MCache.tilesz.x, bounds[1] * MCache.tilesz.y);
        Coord2d maxWorld = new Coord2d(bounds[2] * MCache.tilesz.x, bounds[3] * MCache.tilesz.y);
        
        Coord minScreen = mapView.screenxf(minWorld).round();
        Coord maxScreen = mapView.screenxf(maxWorld).round();
        
        // Draw rectangle
        g.chcolor(color);
        g.rect(minScreen, maxScreen.sub(minScreen));
        
        // Draw field name
        g.chcolor(Color.WHITE);
        Coord center = minScreen.add(maxScreen.sub(minScreen).div(2));
        g.atext(field.getName(), center, 0.5, 0.5);
        
        g.chcolor();
    }
    
    /**
     * Renders a circular field.
     */
    private void renderCircleField(GOut g, MapView mapView, FieldGrid grid, Color color, FarmField field) {
        Coord2d centerGrid = grid.getCenter();
        double radius = grid.getRadius();
        
        // Convert to world coordinates
        Coord2d centerWorld = new Coord2d(centerGrid.x * MCache.tilesz.x, centerGrid.y * MCache.tilesz.y);
        Coord centerScreen = mapView.screenxf(centerWorld).round();
        
        // Calculate screen radius (approximate)
        Coord2d radiusPoint = new Coord2d(centerGrid.x + radius, centerGrid.y);
        Coord2d radiusWorld = new Coord2d(radiusPoint.x * MCache.tilesz.x, radiusPoint.y * MCache.tilesz.y);
        Coord radiusScreen = mapView.screenxf(radiusWorld).round();
        int screenRadius = radiusScreen.sub(centerScreen).x;
        
        // Draw circle
        g.chcolor(color);
        g.fellipse(centerScreen, new Coord(screenRadius, screenRadius));
        
        // Draw field name
        g.chcolor(Color.WHITE);
        g.atext(field.getName(), centerScreen, 0.5, 0.5);
        
        g.chcolor();
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
