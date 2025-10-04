package auto.farming.model;

import java.awt.Color;

/**
 * Enum representing the current operational state of a farm field.
 * 
 * <p>States track the bot's progress through the farming lifecycle and are used
 * for overlay rendering and status display.</p>
 */
public enum FieldState {
    /**
     * Field is idle and not being actively farmed.
     */
    IDLE("Idle", "Field inactive"),
    
    /**
     * Bot is currently tilling soil in the field.
     */
    TILLING("Tilling", "Bot is tilling field", new Color(139, 69, 19)),
    
    /**
     * Bot is currently planting seeds in the field.
     */
    PLANTING("Planting", "Bot is planting crops", new Color(34, 139, 34)),
    
    /**
     * Crops are growing and field is waiting for harvest.
     */
    GROWING("Growing", "Crops are growing", new Color(144, 238, 144)),
    
    /**
     * Crops are ready to be harvested.
     */
    HARVESTABLE("Harvestable", "Ready to harvest", new Color(255, 215, 0)),
    
    /**
     * Bot is currently harvesting crops from the field.
     */
    HARVESTING("Harvesting", "Bot is harvesting", new Color(255, 165, 0)),
    
    /**
     * Bot encountered an error while processing this field.
     */
    ERROR("Error", "Bot encountered an error", new Color(255, 0, 0));
    
    private final String displayName;
    private final String description;
    private final Color overlayColor;
    
    /**
     * Constructs a FieldState with default gray overlay color.
     * 
     * @param displayName Human-readable name for UI display
     * @param description Detailed description of the state
     */
    FieldState(String displayName, String description) {
        this(displayName, description, Color.GRAY);
    }
    
    /**
     * Constructs a FieldState with custom overlay color.
     * 
     * @param displayName Human-readable name for UI display
     * @param description Detailed description of the state
     * @param overlayColor Color used for overlay rendering in MapView
     */
    FieldState(String displayName, String description, Color overlayColor) {
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
     * @return Detailed description of state
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
