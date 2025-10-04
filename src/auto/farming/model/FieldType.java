package auto.farming.model;

import java.awt.Color;

/**
 * Enum representing the type of farming operation for a field.
 * 
 * <p>QUALITY fields focus on selective breeding to increase crop quality over time.
 * QUANTITY fields prioritize mass production with relaxed quality requirements.</p>
 */
public enum FieldType {
    /**
     * Quality breeding field - focuses on increasing crop quality through selective replanting.
     * Only plants crops that meet or exceed quality thresholds.
     */
    QUALITY("Quality Breeding", "Focus on increasing crop quality", new Color(100, 200, 100)),
    
    /**
     * Quantity production field - mass produces crops with relaxed quality requirements.
     * Plants all available seeds regardless of quality for maximum throughput.
     */
    QUANTITY("Quantity Production", "Mass production with relaxed quality", new Color(200, 150, 100));
    
    private final String displayName;
    private final String description;
    private final Color overlayColor;
    
    /**
     * Constructs a FieldType with display information.
     * 
     * @param displayName Human-readable name for UI display
     * @param description Detailed description of the field type's purpose
     * @param overlayColor Color used for overlay rendering in MapView
     */
    FieldType(String displayName, String description, Color overlayColor) {
        this.displayName = displayName;
        this.description = description;
        this.overlayColor = overlayColor;
    }
    
    /**
     * @return Human-readable display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * @return Detailed description of field type
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @return Color for overlay rendering
     */
    public Color getOverlayColor() {
        return overlayColor;
    }
}
