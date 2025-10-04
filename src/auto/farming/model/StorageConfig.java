package auto.farming.model;

import haven.Coord2d;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Configuration for storage chests linked to a farm field.
 * Manages both input chests (seed sources) and output chests (harvest destinations)
 * with support for quality-based sorting.
 */
public class StorageConfig {
    private final UUID fieldId;
    private final List<ChestLink> inputChests;
    private final List<ChestLink> outputChests;
    private double maxChestDistance; // Maximum acceptable distance from field to chest (in tiles)
    
    /**
     * Creates a new storage configuration for the specified field.
     * @param fieldId ID of the field this storage config belongs to
     */
    public StorageConfig(UUID fieldId) {
        this.fieldId = Objects.requireNonNull(fieldId, "Field ID cannot be null");
        this.inputChests = new ArrayList<>();
        this.outputChests = new ArrayList<>();
        this.maxChestDistance = 200.0; // Default: 200 tiles max distance
    }
    
    /**
     * Gets the field ID this storage config belongs to.
     * @return Field UUID
     */
    public UUID getFieldId() {
        return fieldId;
    }
    
    /**
     * Gets the maximum acceptable distance from field to chest.
     * @return Max distance in tiles
     */
    public double getMaxChestDistance() {
        return maxChestDistance;
    }
    
    /**
     * Sets the maximum acceptable distance from field to chest.
     * @param maxChestDistance Max distance in tiles (must be positive)
     */
    public void setMaxChestDistance(double maxChestDistance) {
        if (maxChestDistance <= 0) {
            throw new IllegalArgumentException("Max chest distance must be positive: " + maxChestDistance);
        }
        this.maxChestDistance = maxChestDistance;
    }
    
    /**
     * Adds an input chest (seed source) to this configuration.
     * @param worldCoord World coordinate of the chest
     * @param chestType Type of chest
     * @return The created ChestLink
     * @throws IllegalArgumentException if chest is too far from field
     */
    public ChestLink addInputChest(Coord2d worldCoord, ChestType chestType) {
        ChestLink link = ChestLink.inputChest(worldCoord, chestType);
        inputChests.add(link);
        return link;
    }
    
    /**
     * Adds an output chest (harvest destination) that accepts any quality.
     * @param worldCoord World coordinate of the chest
     * @param chestType Type of chest
     * @return The created ChestLink
     */
    public ChestLink addOutputChest(Coord2d worldCoord, ChestType chestType) {
        ChestLink link = ChestLink.outputChest(worldCoord, chestType);
        outputChests.add(link);
        return link;
    }
    
    /**
     * Adds an output chest with a quality filter.
     * @param worldCoord World coordinate of the chest
     * @param chestType Type of chest
     * @param qualityRule Quality rule for filtering crops
     * @return The created ChestLink
     */
    public ChestLink addOutputChest(Coord2d worldCoord, ChestType chestType, QualityRule qualityRule) {
        ChestLink link = ChestLink.outputChest(worldCoord, chestType, qualityRule);
        outputChests.add(link);
        return link;
    }
    
    /**
     * Removes an input chest from this configuration.
     * @param chest Chest link to remove
     * @return true if removed, false if not found
     */
    public boolean removeInputChest(ChestLink chest) {
        return inputChests.remove(chest);
    }
    
    /**
     * Removes an output chest from this configuration.
     * @param chest Chest link to remove
     * @return true if removed, false if not found
     */
    public boolean removeOutputChest(ChestLink chest) {
        return outputChests.remove(chest);
    }
    
    /**
     * Gets all input chests.
     * @return Unmodifiable list of input chest links
     */
    public List<ChestLink> getInputChests() {
        return Collections.unmodifiableList(inputChests);
    }
    
    /**
     * Gets all output chests.
     * @return Unmodifiable list of output chest links
     */
    public List<ChestLink> getOutputChests() {
        return Collections.unmodifiableList(outputChests);
    }
    
    /**
     * Finds the nearest input chest to a given coordinate.
     * @param coord Coordinate to search from
     * @return Optional containing nearest input chest, empty if no input chests configured
     */
    public Optional<ChestLink> findNearestInputChest(Coord2d coord) {
        return inputChests.stream()
                .min(Comparator.comparingDouble(chest -> chest.distanceTo(coord)));
    }
    
    /**
     * Finds the nearest output chest to a given coordinate.
     * @param coord Coordinate to search from
     * @return Optional containing nearest output chest, empty if no output chests configured
     */
    public Optional<ChestLink> findNearestOutputChest(Coord2d coord) {
        return outputChests.stream()
                .min(Comparator.comparingDouble(chest -> chest.distanceTo(coord)));
    }
    
    /**
     * Finds the nearest output chest that accepts a specific crop quality.
     * @param coord Coordinate to search from
     * @param quality Crop quality value
     * @return Optional containing nearest matching output chest, empty if none match
     */
    public Optional<ChestLink> findNearestOutputChestForQuality(Coord2d coord, int quality) {
        return outputChests.stream()
                .filter(chest -> chest.acceptsQuality(quality))
                .min(Comparator.comparingDouble(chest -> chest.distanceTo(coord)));
    }
    
    /**
     * Gets all output chests that accept a specific quality.
     * @param quality Crop quality value
     * @return List of output chests that accept this quality
     */
    public List<ChestLink> getOutputChestsForQuality(int quality) {
        return outputChests.stream()
                .filter(chest -> chest.acceptsQuality(quality))
                .collect(Collectors.toList());
    }
    
    /**
     * Validates that all chests are within acceptable distance from a coordinate.
     * @param referenceCoord Reference coordinate (typically field center)
     * @return true if all chests are within maxChestDistance, false otherwise
     */
    public boolean validateChestDistances(Coord2d referenceCoord) {
        for (ChestLink chest : inputChests) {
            if (chest.distanceTo(referenceCoord) > maxChestDistance) {
                return false;
            }
        }
        for (ChestLink chest : outputChests) {
            if (chest.distanceTo(referenceCoord) > maxChestDistance) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if any input chests are configured.
     * @return true if at least one input chest exists
     */
    public boolean hasInputChests() {
        return !inputChests.isEmpty();
    }
    
    /**
     * Checks if any output chests are configured.
     * @return true if at least one output chest exists
     */
    public boolean hasOutputChests() {
        return !outputChests.isEmpty();
    }
    
    /**
     * Clears all chest configurations.
     */
    public void clear() {
        inputChests.clear();
        outputChests.clear();
    }
}
