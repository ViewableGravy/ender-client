package auto.farming.model;

/**
 * Enumeration of storage container types used in farming operations.
 */
public enum ChestType {
    /**
     * Standard wooden chest for general storage
     */
    CHEST("Chest", "gfx/terobjs/chest"),
    
    /**
     * Cupboard for organized storage
     */
    CUPBOARD("Cupboard", "gfx/terobjs/cupboard"),
    
    /**
     * Large crate for bulk storage
     */
    CRATE("Crate", "gfx/terobjs/crate"),
    
    /**
     * Animal trough for seed/grain storage
     */
    TROUGH("Trough", "gfx/terobjs/trough"),
    
    /**
     * Barrel for liquid or bulk storage
     */
    BARREL("Barrel", "gfx/terobjs/barrel"),
    
    /**
     * Metal chest for valuable storage
     */
    METAL_CHEST("Metal Chest", "gfx/terobjs/mchest");
    
    private final String displayName;
    private final String resourcePattern;
    
    ChestType(String displayName, String resourcePattern) {
        this.displayName = displayName;
        this.resourcePattern = resourcePattern;
    }
    
    /**
     * Gets the human-readable display name for this chest type.
     * @return Display name (e.g., "Metal Chest")
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the resource name pattern for identifying this chest type.
     * @return Resource name pattern (used for matching Gob resources)
     */
    public String getResourcePattern() {
        return resourcePattern;
    }
    
    /**
     * Attempts to identify chest type from a resource name.
     * @param resourceName Resource name to check
     * @return Matching ChestType if found, null otherwise
     */
    public static ChestType fromResource(String resourceName) {
        if (resourceName == null) {
            return null;
        }
        for (ChestType type : values()) {
            if (resourceName.contains(type.resourcePattern)) {
                return type;
            }
        }
        return null;
    }
}
