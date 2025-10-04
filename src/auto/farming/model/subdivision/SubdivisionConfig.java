package auto.farming.model.subdivision;

/**
 * Configuration for field subdivision.
 * 
 * <p>Defines how a field should be subdivided for crop management.</p>
 */
public class SubdivisionConfig {
    private final SubdivisionType type;
    private final int count;
    
    /**
     * Type of subdivision strategy.
     */
    public enum SubdivisionType {
        /** No subdivision - field is treated as single unit */
        NONE("None"),
        
        /** Rectangular grid subdivision (rows x columns) */
        GRID("Grid"),
        
        /** Radial subdivision (pie slices) for circular fields */
        RADIAL("Radial"),
        
        /** Quadrants (4-way split) */
        QUADRANTS("Quadrants");
        
        private final String displayName;
        
        SubdivisionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Creates a subdivision configuration.
     * 
     * @param type Type of subdivision
     * @param count Number of subdivisions
     */
    public SubdivisionConfig(SubdivisionType type, int count) {
        this.type = type;
        this.count = count;
    }
    
    /**
     * Creates a configuration with no subdivision.
     * 
     * @return Configuration for unsub divided field
     */
    public static SubdivisionConfig none() {
        return new SubdivisionConfig(SubdivisionType.NONE, 1);
    }
    
    /**
     * Creates a quadrant configuration (4 segments).
     * 
     * @return Configuration for 4-way subdivision
     */
    public static SubdivisionConfig quadrants() {
        return new SubdivisionConfig(SubdivisionType.QUADRANTS, 4);
    }
    
    /**
     * @return Subdivision type
     */
    public SubdivisionType getType() {
        return type;
    }
    
    /**
     * @return Number of subdivisions
     */
    public int getCount() {
        return count;
    }
    
    @Override
    public String toString() {
        return String.format("SubdivisionConfig[type=%s, count=%d]", type, count);
    }
}
