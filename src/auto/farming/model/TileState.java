package auto.farming.model;

import haven.Coord2d;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Tracks the state of an individual tile within a farm field.
 * Includes position, crop information, growth stage, and quality data.
 */
public class TileState {
    private final Coord2d gridCoord;
    private TileStateEnum state;
    private String cropType; // Resource name (e.g., "gfx/terobjs/plants/wheat")
    private Integer cropQuality; // Quality value if known (-4 to +5 for quantity, 0 to +5 for quality breeding)
    private int growthStage; // 0 to max stage for crop (from CropConfig)
    private Instant lastUpdated;
    
    /**
     * Creates a tile state for the specified grid coordinate.
     * @param gridCoord Grid coordinate of this tile
     * @param state Initial state of the tile
     */
    public TileState(Coord2d gridCoord, TileStateEnum state) {
        this.gridCoord = Objects.requireNonNull(gridCoord, "Grid coordinate cannot be null");
        this.state = Objects.requireNonNull(state, "Tile state cannot be null");
        this.cropType = null;
        this.cropQuality = null;
        this.growthStage = 0;
        this.lastUpdated = Instant.now();
    }
    
    /**
     * Gets the grid coordinate of this tile.
     * @return Grid coordinate (immutable)
     */
    public Coord2d getGridCoord() {
        return gridCoord;
    }
    
    /**
     * Gets the current state of this tile.
     * @return Tile state enum
     */
    public TileStateEnum getState() {
        return state;
    }
    
    /**
     * Updates the state of this tile and refreshes the timestamp.
     * @param state New tile state
     */
    public void setState(TileStateEnum state) {
        this.state = Objects.requireNonNull(state, "Tile state cannot be null");
        this.lastUpdated = Instant.now();
    }
    
    /**
     * Gets the crop type planted on this tile.
     * @return Optional containing crop resource name if planted, empty otherwise
     */
    public Optional<String> getCropType() {
        return Optional.ofNullable(cropType);
    }
    
    /**
     * Sets the crop type for this tile.
     * @param cropType Crop resource name (e.g., "gfx/terobjs/plants/wheat"), or null
     */
    public void setCropType(String cropType) {
        this.cropType = cropType;
        this.lastUpdated = Instant.now();
    }
    
    /**
     * Gets the quality of the planted crop if known.
     * @return Optional containing quality value if known, empty otherwise
     */
    public Optional<Integer> getCropQuality() {
        return Optional.ofNullable(cropQuality);
    }
    
    /**
     * Sets the crop quality for this tile.
     * @param cropQuality Quality value (-4 to +5), or null if unknown
     */
    public void setCropQuality(Integer cropQuality) {
        this.cropQuality = cropQuality;
        this.lastUpdated = Instant.now();
    }
    
    /**
     * Gets the current growth stage of the crop.
     * @return Growth stage (0 = just planted, max = ready for harvest)
     */
    public int getGrowthStage() {
        return growthStage;
    }
    
    /**
     * Sets the growth stage for the crop on this tile.
     * @param growthStage Growth stage (0 to max for crop type)
     */
    public void setGrowthStage(int growthStage) {
        if (growthStage < 0) {
            throw new IllegalArgumentException("Growth stage cannot be negative: " + growthStage);
        }
        this.growthStage = growthStage;
        this.lastUpdated = Instant.now();
    }
    
    /**
     * Gets the timestamp of the last update to this tile.
     * @return Last updated instant
     */
    public Instant getLastUpdated() {
        return lastUpdated;
    }
    
    /**
     * Clears all crop-related information from this tile.
     * Useful when harvesting or clearing a tile.
     */
    public void clearCrop() {
        this.cropType = null;
        this.cropQuality = null;
        this.growthStage = 0;
        this.lastUpdated = Instant.now();
    }
    
    /**
     * Checks if this tile has a crop planted on it.
     * @return true if crop type is set
     */
    public boolean hasCrop() {
        return cropType != null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TileState tileState = (TileState) o;
        return gridCoord.equals(tileState.gridCoord);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(gridCoord);
    }
    
    @Override
    public String toString() {
        return "TileState{" +
                "coord=" + gridCoord +
                ", state=" + state +
                ", crop=" + cropType +
                ", quality=" + cropQuality +
                ", stage=" + growthStage +
                '}';
    }
}
