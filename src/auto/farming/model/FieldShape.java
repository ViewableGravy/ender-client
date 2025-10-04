package auto.farming.model;

/**
 * Enum representing the geometric shape of a farm field.
 * 
 * <p>Different shapes support different planting strategies and use cases.</p>
 */
public enum FieldShape {
    /**
     * Rectangular field - standard shape for most farming operations.
     * Easy to define and subdivide into quadrants.
     */
    RECTANGLE("Rectangle", "Standard rectangular field"),
    
    /**
     * Circular field - optimized for quality breeding operations.
     * Can be subdivided into segments for better crop management.
     */
    CIRCLE("Circle", "Circular field for quality breeding");
    
    private final String displayName;
    private final String description;
    
    /**
     * Constructs a FieldShape with display information.
     * 
     * @param displayName Human-readable name for UI display
     * @param description Detailed description of the shape type
     */
    FieldShape(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * @return Human-readable display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * @return Detailed description of shape
     */
    public String getDescription() {
        return description;
    }
}
