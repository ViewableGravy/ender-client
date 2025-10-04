package auto.farming.ui.overlay;

import auto.farming.FarmingManager;
import auto.farming.model.*;
import auto.farming.model.subdivision.*;
import haven.*;

import java.awt.*;
import java.util.Optional;

/**
 * Main overlay renderer for the farming bot.
 * Draws field boundaries, subdivisions, and tile states on the MapView.
 */
public class FarmingOverlay {
    private static final FarmingOverlay INSTANCE = new FarmingOverlay();
    
    private boolean enabled = false;
    private boolean showTileStates = false; // Toggle for tile state visualization
    private boolean showBotActivity = false; // Toggle for bot activity indicators
    private final FarmingManager manager;
    private final OverlaySettings settings;
    
    // Bot activity tracking
    private Coord2d currentTargetTile = null;
    private FarmField activeField = null;
    
    // Performance tracking
    private long lastRenderTime = 0;
    private int renderedFieldsCount = 0;
    private boolean showPerformanceMetrics = false;
    
    // Visual settings
    private static final Color RECTANGLE_COLOR = new Color(0, 255, 0, 128); // Green with alpha
    private static final Color CIRCLE_COLOR = new Color(0, 200, 255, 128); // Cyan with alpha
    private static final float LINE_WIDTH = 2.0f;
    
    /**
     * Private constructor for singleton pattern.
     */
    private FarmingOverlay() {
        this.manager = FarmingManager.getInstance();
        this.settings = new OverlaySettings();
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
     * Get the current overlay settings.
     * @return The overlay settings
     */
    public OverlaySettings getSettings() {
        return settings;
    }
    
    /**
     * Update overlay settings and apply changes.
     * @param newSettings The new settings to apply
     */
    public void updateSettings(OverlaySettings newSettings) {
        // Copy settings
        settings.setShowFieldBoundaries(newSettings.isShowFieldBoundaries());
        settings.setShowSubdivisions(newSettings.isShowSubdivisions());
        settings.setShowTileStates(newSettings.isShowTileStates());
        settings.setShowBotActivity(newSettings.isShowBotActivity());
        settings.setShowPerformanceMetrics(newSettings.isShowPerformanceMetrics());
        
        settings.setQualityFieldColor(newSettings.getQualityFieldColor());
        settings.setQuantityFieldColor(newSettings.getQuantityFieldColor());
        settings.setSubdivisionColor(newSettings.getSubdivisionColor());
        settings.setActiveFieldColor(newSettings.getActiveFieldColor());
        
        settings.setFieldBorderWidth(newSettings.getFieldBorderWidth());
        settings.setSubdivisionBorderWidth(newSettings.getSubdivisionBorderWidth());
        settings.setActiveFieldBorderWidth(newSettings.getActiveFieldBorderWidth());
        settings.setFieldOpacity(newSettings.getFieldOpacity());
        
        // Update internal toggles to match settings
        showTileStates = settings.isShowTileStates();
        showBotActivity = settings.isShowBotActivity();
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
        
        long startTime = System.nanoTime();
        renderedFieldsCount = 0;
        
        try {
            // Render all enabled fields
            for (FarmField field : manager.getEnabledFields()) {
                // Simple frustum culling: check if field is roughly on screen
                if (isFieldVisible(mapView, field)) {
                    renderField(g, mapView, field);
                    renderedFieldsCount++;
                }
            }
            
            // Render performance metrics if enabled
            if (settings.isShowPerformanceMetrics() && showPerformanceMetrics) {
                long renderTime = (System.nanoTime() - startTime) / 1_000_000; // Convert to ms
                renderPerformanceMetrics(g, renderTime);
            }
            
            lastRenderTime = System.nanoTime() - startTime;
        } catch (Exception e) {
            // Catch any rendering errors to avoid crashing the client
            System.err.println("Error rendering farming overlay: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Simple visibility check for frustum culling.
     * Checks if a field is roughly within the visible viewport.
     */
    private boolean isFieldVisible(MapView mapView, FarmField field) {
        FieldGrid grid = field.getGrid();
        if (grid == null) {
            return false;
        }
        
        // Get field center
        Coord2d centerGrid = grid.getCenter();
        Coord2d centerWorld = new Coord2d(centerGrid.x * 11.0, centerGrid.y * 11.0);
        
        try {
            Coord3f center3d = mapView.glob.map.getzp(centerWorld);
            Coord3f centerScreen = mapView.screenxf(center3d);
            
            // If center projects to null, field is not visible
            if (centerScreen == null) {
                return false;
            }
            
            // Simple on-screen check (with generous margin for field size)
            int margin = 500; // pixels
            return centerScreen.x > -margin && centerScreen.x < mapView.sz.x + margin &&
                   centerScreen.y > -margin && centerScreen.y < mapView.sz.y + margin;
        } catch (Loading e) {
            // If map data not loaded, assume visible to try again next frame
            return true;
        }
    }
    
    /**
     * Renders performance metrics overlay.
     */
    private void renderPerformanceMetrics(GOut g, long renderTimeMs) {
        String metricsText = String.format("Farming Overlay: %dms | Fields: %d", 
                                          renderTimeMs, renderedFieldsCount);
        
        g.chcolor(new Color(0, 0, 0, 180));
        g.frect(new Coord(10, 10), new Coord(250, 20));
        g.chcolor(new Color(0, 255, 0, 255));
        g.atext(metricsText, new Coord(15, 22), 0.0, 0.0);
        g.chcolor();
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
        
        // Render field boundaries if enabled
        if (settings.isShowFieldBoundaries()) {
            if (field.getShape() == FieldShape.RECTANGLE) {
                renderRectangleField(g, mapView, grid, fieldColor, field);
            } else if (field.getShape() == FieldShape.CIRCLE) {
                renderCircleField(g, mapView, grid, fieldColor, field);
            }
        }
        
        // Render subdivisions if enabled and present
        if (settings.isShowSubdivisions()) {
            renderSubdivisions(g, mapView, field);
        }
        
        // Render tile states if enabled
        if (settings.isShowTileStates() && showTileStates) {
            renderTileStates(g, mapView, field);
        }
        
        // Render bot activity indicators if enabled
        if (settings.isShowBotActivity() && showBotActivity && activeField != null && activeField.getId().equals(field.getId())) {
            renderBotActivity(g, mapView, field);
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
            int borderWidth = settings.getFieldBorderWidth();
            g.line(tl, tr, borderWidth); // Top edge
            g.line(tr, br, borderWidth); // Right edge
            g.line(br, bl, borderWidth); // Bottom edge
            g.line(bl, tl, borderWidth); // Left edge
            
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
            int borderWidth = settings.getFieldBorderWidth();
            for (int i = 0; i < segments; i++) {
                int next = (i + 1) % segments;
                g.line(points[i], points[next], borderWidth);
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
            return settings.getQualityFieldColor();
        } else {
            return settings.getQuantityFieldColor();
        }
    }
    
    /**
     * Renders subdivision lines within a field.
     */
    private void renderSubdivisions(GOut g, MapView mapView, FarmField field) {
        SubdivisionConfig subdivConfig = field.getSubdivisionConfig();
        if (subdivConfig == null || subdivConfig.getType() == SubdivisionConfig.SubdivisionType.NONE) {
            return;
        }
        
        // Create subdivision instance based on config
        FieldSubdivision subdivision = createSubdivision(field.getGrid(), subdivConfig);
        if (subdivision == null) {
            return;
        }
        
        Color subdivColor = settings.getSubdivisionColor();
        
        // Render each subdivision segment
        for (int i = 0; i < subdivision.getCount(); i++) {
            FieldGrid segment = subdivision.getSubdivision(i);
            renderSubdivisionSegment(g, mapView, segment, subdivColor, i);
        }
    }
    
    /**
     * Creates a subdivision instance from config.
     */
    private FieldSubdivision createSubdivision(FieldGrid grid, SubdivisionConfig config) {
        if (grid == null || config == null) {
            return null;
        }
        
        switch (config.getType()) {
            case QUADRANTS:
                if (grid.getShape() == FieldShape.RECTANGLE) {
                    return new RectangleSubdivision(grid, 2, 2);
                } else if (grid.getShape() == FieldShape.CIRCLE) {
                    return new CircleSubdivision(grid, 4);
                }
                break;
            case GRID:
                // For grid, we'd need rows/columns info which isn't in SubdivisionConfig
                // Default to 2x2 for now
                if (grid.getShape() == FieldShape.RECTANGLE) {
                    return new RectangleSubdivision(grid, 2, 2);
                }
                break;
            case RADIAL:
                if (grid.getShape() == FieldShape.CIRCLE) {
                    return new CircleSubdivision(grid, config.getCount());
                }
                break;
            case NONE:
            default:
                return null;
        }
        return null;
    }
    
    /**
     * Renders a single subdivision segment.
     */
    private void renderSubdivisionSegment(GOut g, MapView mapView, FieldGrid segment, Color color, int index) {
        if (segment.getShape() == FieldShape.RECTANGLE) {
            renderRectangleSubdivision(g, mapView, segment, color, index);
        } else if (segment.getShape() == FieldShape.CIRCLE) {
            renderCircleSubdivision(g, mapView, segment, color, index);
        }
    }
    
    /**
     * Renders a rectangular subdivision segment.
     */
    private void renderRectangleSubdivision(GOut g, MapView mapView, FieldGrid grid, Color color, int index) {
        double[] bounds = grid.getBounds();
        
        // Convert grid coordinates to world coordinates
        double minX = bounds[0] * 11.0;
        double minY = bounds[1] * 11.0;
        double maxX = bounds[2] * 11.0;
        double maxY = bounds[3] * 11.0;
        
        // Get all 4 corners in world space
        Coord2d topLeft = new Coord2d(minX, minY);
        Coord2d topRight = new Coord2d(maxX, minY);
        Coord2d bottomLeft = new Coord2d(minX, maxY);
        Coord2d bottomRight = new Coord2d(maxX, maxY);
        
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
            
            // Draw subdivision boundary with subdivision line width
            g.chcolor(color);
            int subdivisionWidth = settings.getSubdivisionBorderWidth();
            g.line(tl, tr, subdivisionWidth); // Top edge
            g.line(tr, br, subdivisionWidth); // Right edge
            g.line(br, bl, subdivisionWidth); // Bottom edge
            g.line(bl, tl, subdivisionWidth); // Left edge
            
            g.chcolor();
        } catch (Loading e) {
            // Map data not loaded yet
        }
    }
    
    /**
     * Renders a circular subdivision segment (pie slice).
     */
    private void renderCircleSubdivision(GOut g, MapView mapView, FieldGrid grid, Color color, int index) {
        // For circular subdivisions, we render the arc boundaries
        // This is more complex and may be simplified in initial implementation
        // For now, just render the segment as a small circle at its center
        Coord2d centerGrid = grid.getCenter();
        Coord2d centerWorld = new Coord2d(centerGrid.x * 11.0, centerGrid.y * 11.0);
        
        try {
            Coord3f center3d = mapView.glob.map.getzp(centerWorld);
            Coord3f centerScreen = mapView.screenxf(center3d);
            
            if (centerScreen != null) {
                g.chcolor(color);
                // Draw a small marker at the subdivision center
                Coord center = centerScreen.round2();
                g.fellipse(center, new Coord(3, 3));
                g.chcolor();
            }
        } catch (Loading e) {
            // Map data not loaded yet
        }
    }
    
    /**
     * Renders tile states for a field.
     */
    private void renderTileStates(GOut g, MapView mapView, FarmField field) {
        FieldTileMap tileMap = field.getTileMap();
        if (tileMap == null) {
            return;
        }
        
        FieldGrid grid = field.getGrid();
        double[] bounds = grid.getBounds();
        
        // Iterate through tiles in the field
        int minX = (int) Math.floor(bounds[0]);
        int minY = (int) Math.floor(bounds[1]);
        int maxX = (int) Math.ceil(bounds[2]);
        int maxY = (int) Math.ceil(bounds[3]);
        
        try {
            for (int gridX = minX; gridX <= maxX; gridX++) {
                for (int gridY = minY; gridY <= maxY; gridY++) {
                    // Check if tile is within field bounds
                    Coord2d tileCoord = new Coord2d(gridX, gridY);
                    if (!grid.contains(tileCoord)) {
                        continue;
                    }
                    
                    Optional<TileState> tileStateOpt = tileMap.getTile(tileCoord);
                    if (tileStateOpt.isPresent()) {
                        renderTile(g, mapView, gridX, gridY, tileStateOpt.get());
                    }
                }
            }
        } catch (Exception e) {
            // Ignore rendering errors for individual tiles
        }
    }
    
    /**
     * Renders a single tile with its state.
     */
    private void renderTile(GOut g, MapView mapView, int gridX, int gridY, TileState tileState) {
        // Convert grid coordinates to world coordinates
        double worldX = gridX * 11.0;
        double worldY = gridY * 11.0;
        double tileSize = 11.0;
        
        // Get the 4 corners of the tile
        Coord2d tl = new Coord2d(worldX, worldY);
        Coord2d tr = new Coord2d(worldX + tileSize, worldY);
        Coord2d bl = new Coord2d(worldX, worldY + tileSize);
        Coord2d br = new Coord2d(worldX + tileSize, worldY + tileSize);
        
        try {
            // Get terrain heights
            Coord3f tl3d = mapView.glob.map.getzp(tl);
            Coord3f tr3d = mapView.glob.map.getzp(tr);
            Coord3f bl3d = mapView.glob.map.getzp(bl);
            Coord3f br3d = mapView.glob.map.getzp(br);
            
            // Project to screen
            Coord3f tlScreen = mapView.screenxf(tl3d);
            Coord3f trScreen = mapView.screenxf(tr3d);
            Coord3f blScreen = mapView.screenxf(bl3d);
            Coord3f brScreen = mapView.screenxf(br3d);
            
            if (tlScreen == null || trScreen == null || blScreen == null || brScreen == null) {
                return;
            }
            
            // Get color based on tile state
            Color tileColor = getTileStateColor(tileState.getState());
            
            // Draw filled rectangle for tile
            // Since there's no poly(), we'll draw 4 lines to outline the tile
            Coord tlCoord = tlScreen.round2();
            Coord trCoord = trScreen.round2();
            Coord blCoord = blScreen.round2();
            Coord brCoord = brScreen.round2();
            
            g.chcolor(tileColor);
            g.line(tlCoord, trCoord, 1);
            g.line(trCoord, brCoord, 1);
            g.line(brCoord, blCoord, 1);
            g.line(blCoord, tlCoord, 1);
            g.chcolor();
            
        } catch (Loading e) {
            // Map data not loaded yet
        }
    }
    
    /**
     * Gets the color for a tile state.
     */
    private Color getTileStateColor(TileStateEnum state) {
        switch (state) {
            case UNTILLED:
                return new Color(139, 69, 19, 60); // Brown, semi-transparent
            case TILLED:
                return new Color(160, 82, 45, 80); // Darker brown
            case PLANTED:
                return new Color(144, 238, 144, 100); // Light green
            case GROWING:
                return new Color(34, 139, 34, 100); // Forest green
            case HARVESTABLE:
                return new Color(255, 215, 0, 120); // Gold
            case UNKNOWN:
            default:
                return new Color(128, 128, 128, 50); // Gray, very transparent
        }
    }
    
    /**
     * Toggles tile state visualization.
     */
    public void toggleTileStates() {
        showTileStates = !showTileStates;
        System.out.println("Tile state visualization " + (showTileStates ? "enabled" : "disabled"));
    }
    
    /**
     * Sets whether tile states should be shown.
     */
    public void setShowTileStates(boolean show) {
        this.showTileStates = show;
    }
    
    /**
     * Checks if tile states are being shown.
     */
    public boolean isShowingTileStates() {
        return showTileStates;
    }
    
    /**
     * Renders bot activity indicators for the active field.
     */
    private void renderBotActivity(GOut g, MapView mapView, FarmField field) {
        // Render pulsing border around active field
        renderPulsingBorder(g, mapView, field);
        
        // Render target tile indicator if set
        if (currentTargetTile != null) {
            renderTargetTile(g, mapView, currentTargetTile);
        }
    }
    
    /**
     * Renders a pulsing border around the active field.
     */
    private void renderPulsingBorder(GOut g, MapView mapView, FarmField field) {
        FieldGrid grid = field.getGrid();
        
        // Calculate pulsing alpha based on time (sine wave)
        long currentTime = System.currentTimeMillis();
        double pulse = Math.sin(currentTime / 500.0) * 0.5 + 0.5; // 0.0 to 1.0
        int baseAlpha = settings.getActiveFieldColor().getAlpha();
        int alpha = (int) (baseAlpha * (0.5 + pulse * 0.5)); // Pulse between 50% and 100% of base alpha
        
        Color activeColor = settings.getActiveFieldColor();
        Color pulseColor = new Color(activeColor.getRed(), activeColor.getGreen(), activeColor.getBlue(), alpha);
        int width = settings.getActiveFieldBorderWidth();
        
        if (field.getShape() == FieldShape.RECTANGLE) {
            renderRectangleBorder(g, mapView, grid, pulseColor, width);
        } else if (field.getShape() == FieldShape.CIRCLE) {
            renderCircleBorder(g, mapView, grid, pulseColor, width);
        }
    }
    
    /**
     * Renders a rectangle border (helper for pulsing border).
     */
    private void renderRectangleBorder(GOut g, MapView mapView, FieldGrid grid, Color color, int width) {
        double[] bounds = grid.getBounds();
        double minX = bounds[0] * 11.0;
        double minY = bounds[1] * 11.0;
        double maxX = bounds[2] * 11.0;
        double maxY = bounds[3] * 11.0;
        
        try {
            Coord3f tl3d = mapView.glob.map.getzp(new Coord2d(minX, minY));
            Coord3f tr3d = mapView.glob.map.getzp(new Coord2d(maxX, minY));
            Coord3f bl3d = mapView.glob.map.getzp(new Coord2d(minX, maxY));
            Coord3f br3d = mapView.glob.map.getzp(new Coord2d(maxX, maxY));
            
            Coord3f tlScreen = mapView.screenxf(tl3d);
            Coord3f trScreen = mapView.screenxf(tr3d);
            Coord3f blScreen = mapView.screenxf(bl3d);
            Coord3f brScreen = mapView.screenxf(br3d);
            
            if (tlScreen != null && trScreen != null && blScreen != null && brScreen != null) {
                g.chcolor(color);
                g.line(tlScreen.round2(), trScreen.round2(), width);
                g.line(trScreen.round2(), brScreen.round2(), width);
                g.line(brScreen.round2(), blScreen.round2(), width);
                g.line(blScreen.round2(), tlScreen.round2(), width);
                g.chcolor();
            }
        } catch (Loading e) {
            // Map not loaded
        }
    }
    
    /**
     * Renders a circle border (helper for pulsing border).
     */
    private void renderCircleBorder(GOut g, MapView mapView, FieldGrid grid, Color color, int width) {
        Coord2d centerGrid = grid.getCenter();
        double radius = grid.getRadius();
        double centerWorldX = centerGrid.x * 11.0;
        double centerWorldY = centerGrid.y * 11.0;
        double radiusWorld = radius * 11.0;
        
        int segments = 32;
        Coord[] points = new Coord[segments];
        
        try {
            for (int i = 0; i < segments; i++) {
                double angle = (2 * Math.PI * i) / segments;
                double x = centerWorldX + radiusWorld * Math.cos(angle);
                double y = centerWorldY + radiusWorld * Math.sin(angle);
                
                Coord3f point3d = mapView.glob.map.getzp(new Coord2d(x, y));
                Coord3f screenPoint = mapView.screenxf(point3d);
                
                if (screenPoint == null) {
                    return;
                }
                
                points[i] = screenPoint.round2();
            }
            
            g.chcolor(color);
            for (int i = 0; i < segments; i++) {
                int next = (i + 1) % segments;
                g.line(points[i], points[next], width);
            }
            g.chcolor();
        } catch (Loading e) {
            // Map not loaded
        }
    }
    
    /**
     * Renders an animated indicator on the target tile.
     */
    private void renderTargetTile(GOut g, MapView mapView, Coord2d tileCoord) {
        double worldX = tileCoord.x * 11.0;
        double worldY = tileCoord.y * 11.0;
        double tileSize = 11.0;
        
        // Center of the tile
        Coord2d tileCenter = new Coord2d(worldX + tileSize / 2, worldY + tileSize / 2);
        
        try {
            Coord3f center3d = mapView.glob.map.getzp(tileCenter);
            Coord3f centerScreen = mapView.screenxf(center3d);
            
            if (centerScreen != null) {
                Coord center = centerScreen.round2();
                
                // Animate marker size (pulsing)
                long currentTime = System.currentTimeMillis();
                double pulse = Math.sin(currentTime / 300.0) * 0.5 + 0.5;
                int size = (int) (8 + pulse * 8); // 8 to 16 pixels
                
                // Draw bright marker
                g.chcolor(new Color(255, 255, 0, 255)); // Yellow outline (larger)
                g.fellipse(center, new Coord(size + 2, size + 2));
                g.chcolor(new Color(255, 0, 0, 200)); // Bright red center
                g.fellipse(center, new Coord(size, size));
                g.chcolor();
            }
        } catch (Loading e) {
            // Map not loaded
        }
    }
    
    /**
     * Sets the active field for bot activity indicators.
     */
    public void setActiveField(FarmField field) {
        this.activeField = field;
    }
    
    /**
     * Sets the current target tile for bot activity indicators.
     */
    public void setCurrentTargetTile(Coord2d tile) {
        this.currentTargetTile = tile;
    }
    
    /**
     * Toggles bot activity indicators.
     */
    public void toggleBotActivity() {
        showBotActivity = !showBotActivity;
        System.out.println("Bot activity indicators " + (showBotActivity ? "enabled" : "disabled"));
    }
    
    /**
     * Sets whether bot activity should be shown.
     */
    public void setShowBotActivity(boolean show) {
        this.showBotActivity = show;
    }
    
    /**
     * Checks if bot activity is being shown.
     */
    public boolean isShowingBotActivity() {
        return showBotActivity;
    }
    
    /**
     * Toggles performance metrics display.
     */
    public void togglePerformanceMetrics() {
        showPerformanceMetrics = !showPerformanceMetrics;
        System.out.println("Performance metrics " + (showPerformanceMetrics ? "enabled" : "disabled"));
    }
    
    /**
     * Sets whether performance metrics should be shown.
     */
    public void setShowPerformanceMetrics(boolean show) {
        this.showPerformanceMetrics = show;
    }
    
    /**
     * Gets the last render time in nanoseconds.
     */
    public long getLastRenderTimeNanos() {
        return lastRenderTime;
    }
    
    /**
     * Gets the number of fields rendered in the last frame.
     */
    public int getRenderedFieldsCount() {
        return renderedFieldsCount;
    }
}

