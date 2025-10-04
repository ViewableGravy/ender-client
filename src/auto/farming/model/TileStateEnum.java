package auto.farming.model;

/**
 * Enumeration representing the state of an individual farm tile.
 * Tracks lifecycle from untilled ground through harvesting.
 */
public enum TileStateEnum {
    /**
     * Empty tile, needs plowing/tilling before planting
     */
    UNTILLED("Untilled", "Empty ground that needs plowing"),
    
    /**
     * Plowed/tilled tile, ready for seed planting
     */
    TILLED("Tilled", "Plowed ground ready for planting"),
    
    /**
     * Seed has been planted, crop is beginning to grow
     */
    PLANTED("Planted", "Seed planted, beginning growth"),
    
    /**
     * Crop is growing, at an intermediate stage
     * Growth stage number tracked separately in TileState
     */
    GROWING("Growing", "Crop actively growing"),
    
    /**
     * Crop is fully grown and ready to harvest
     */
    HARVESTABLE("Harvestable", "Crop ready for harvest"),
    
    /**
     * Tile state cannot be determined (not visible, fog of war, etc.)
     */
    UNKNOWN("Unknown", "Tile state cannot be determined");
    
    private final String displayName;
    private final String description;
    
    TileStateEnum(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Gets the human-readable display name for this tile state.
     * @return Display name (e.g., "Harvestable")
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description of this tile state.
     * @return Description of what this state represents
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if this tile is ready for planting.
     * @return true if state is TILLED
     */
    public boolean isReadyForPlanting() {
        return this == TILLED;
    }
    
    /**
     * Checks if this tile has a crop growing on it.
     * @return true if state is PLANTED, GROWING, or HARVESTABLE
     */
    public boolean hasCrop() {
        return this == PLANTED || this == GROWING || this == HARVESTABLE;
    }
    
    /**
     * Checks if this tile is ready for harvest.
     * @return true if state is HARVESTABLE
     */
    public boolean isReadyForHarvest() {
        return this == HARVESTABLE;
    }
}
