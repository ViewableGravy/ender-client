package auto.farming.model;

import haven.Coord2d;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a link to a storage chest used for farming operations.
 * Can be configured as an input chest (seed source) or output chest (harvest destination).
 */
public class ChestLink {
    private final Coord2d worldCoord;
    private final ChestType chestType;
    private final boolean isInput;
    private final QualityRule qualityRule; // Null for input chests, optional for output chests
    
    /**
     * Creates a new chest link.
     * @param worldCoord World coordinate of the chest
     * @param chestType Type of chest
     * @param isInput true if this is an input chest (seed source), false for output (harvest destination)
     * @param qualityRule Quality filter rule (null for input chests or output chests that accept all qualities)
     */
    public ChestLink(Coord2d worldCoord, ChestType chestType, boolean isInput, QualityRule qualityRule) {
        this.worldCoord = Objects.requireNonNull(worldCoord, "World coordinate cannot be null");
        this.chestType = Objects.requireNonNull(chestType, "Chest type cannot be null");
        this.isInput = isInput;
        this.qualityRule = qualityRule;
    }
    
    /**
     * Creates an input chest link (seed source).
     * @param worldCoord World coordinate of the chest
     * @param chestType Type of chest
     * @return New input chest link
     */
    public static ChestLink inputChest(Coord2d worldCoord, ChestType chestType) {
        return new ChestLink(worldCoord, chestType, true, null);
    }
    
    /**
     * Creates an output chest link that accepts any quality.
     * @param worldCoord World coordinate of the chest
     * @param chestType Type of chest
     * @return New output chest link
     */
    public static ChestLink outputChest(Coord2d worldCoord, ChestType chestType) {
        return new ChestLink(worldCoord, chestType, false, null);
    }
    
    /**
     * Creates an output chest link with a quality filter.
     * @param worldCoord World coordinate of the chest
     * @param chestType Type of chest
     * @param qualityRule Quality rule for filtering crops
     * @return New output chest link with quality filter
     */
    public static ChestLink outputChest(Coord2d worldCoord, ChestType chestType, QualityRule qualityRule) {
        return new ChestLink(worldCoord, chestType, false, qualityRule);
    }
    
    /**
     * Gets the world coordinate of this chest.
     * @return World coordinate
     */
    public Coord2d getWorldCoord() {
        return worldCoord;
    }
    
    /**
     * Gets the type of this chest.
     * @return Chest type
     */
    public ChestType getChestType() {
        return chestType;
    }
    
    /**
     * Checks if this is an input chest (seed source).
     * @return true if input chest, false if output chest
     */
    public boolean isInput() {
        return isInput;
    }
    
    /**
     * Checks if this is an output chest (harvest destination).
     * @return true if output chest, false if input chest
     */
    public boolean isOutput() {
        return !isInput;
    }
    
    /**
     * Gets the quality rule for this chest link.
     * @return Optional containing quality rule if set, empty otherwise
     */
    public Optional<QualityRule> getQualityRule() {
        return Optional.ofNullable(qualityRule);
    }
    
    /**
     * Checks if a crop quality matches this chest's quality rule.
     * Always returns true for input chests or output chests without quality rules.
     * @param quality Crop quality value
     * @return true if quality matches (or no rule is set)
     */
    public boolean acceptsQuality(int quality) {
        if (isInput || qualityRule == null) {
            return true;
        }
        return qualityRule.matches(quality);
    }
    
    /**
     * Calculates the distance from this chest to a given coordinate.
     * @param coord Coordinate to measure distance to
     * @return Euclidean distance
     */
    public double distanceTo(Coord2d coord) {
        return worldCoord.dist(coord);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChestLink chestLink = (ChestLink) o;
        return worldCoord.equals(chestLink.worldCoord);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(worldCoord);
    }
    
    @Override
    public String toString() {
        return "ChestLink{" +
                "coord=" + worldCoord +
                ", type=" + chestType +
                ", " + (isInput ? "input" : "output") +
                (qualityRule != null ? ", rule=" + qualityRule : "") +
                '}';
    }
}
