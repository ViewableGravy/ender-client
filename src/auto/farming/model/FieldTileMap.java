package auto.farming.model;

import haven.Coord2d;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages the state of all tiles within a farm field.
 * Provides efficient lookups and queries for tile states.
 */
public class FieldTileMap {
    private final FarmField field;
    private final Map<Coord2d, TileState> tileMap;
    
    /**
     * Creates a new tile map for the specified field.
     * @param field The farm field this tile map belongs to
     */
    public FieldTileMap(FarmField field) {
        this.field = Objects.requireNonNull(field, "Field cannot be null");
        this.tileMap = new ConcurrentHashMap<>();
        initializeTiles();
    }
    
    /**
     * Initializes all tiles within the field's grid to UNKNOWN state.
     * Called during construction to populate the tile map.
     */
    private void initializeTiles() {
        if (field.getGrid() != null) {
            for (Coord2d tileCoord : field.getGrid().getTiles()) {
                tileMap.put(tileCoord, new TileState(tileCoord, TileStateEnum.UNKNOWN));
            }
        }
    }
    
    /**
     * Gets the state of the tile at the specified coordinate.
     * @param coord Grid coordinate
     * @return TileState if tile exists in field, empty otherwise
     */
    public Optional<TileState> getTile(Coord2d coord) {
        return Optional.ofNullable(tileMap.get(coord));
    }
    
    /**
     * Updates the state of a tile at the specified coordinate.
     * If the tile doesn't exist in the map, it will be created.
     * @param coord Grid coordinate
     * @param state New tile state
     */
    public void updateTile(Coord2d coord, TileState state) {
        Objects.requireNonNull(coord, "Coordinate cannot be null");
        Objects.requireNonNull(state, "Tile state cannot be null");
        tileMap.put(coord, state);
    }
    
    /**
     * Updates the state enum of a tile at the specified coordinate.
     * @param coord Grid coordinate
     * @param state New state enum value
     */
    public void updateTileState(Coord2d coord, TileStateEnum state) {
        Objects.requireNonNull(coord, "Coordinate cannot be null");
        Objects.requireNonNull(state, "State cannot be null");
        
        TileState tile = tileMap.computeIfAbsent(coord, c -> new TileState(c, TileStateEnum.UNKNOWN));
        tile.setState(state);
    }
    
    /**
     * Gets all tiles that match the specified state.
     * @param state State to filter by
     * @return List of tiles in the specified state
     */
    public List<TileState> getTilesByState(TileStateEnum state) {
        Objects.requireNonNull(state, "State cannot be null");
        return tileMap.values().stream()
                .filter(tile -> tile.getState() == state)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all tiles that have crops planted on them.
     * @return List of tiles with crops
     */
    public List<TileState> getTilesWithCrops() {
        return tileMap.values().stream()
                .filter(TileState::hasCrop)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all tiles planted with a specific crop type.
     * @param cropType Crop resource name
     * @return List of tiles with the specified crop
     */
    public List<TileState> getTilesByCropType(String cropType) {
        Objects.requireNonNull(cropType, "Crop type cannot be null");
        return tileMap.values().stream()
                .filter(tile -> tile.getCropType().filter(cropType::equals).isPresent())
                .collect(Collectors.toList());
    }
    
    /**
     * Performs a bulk update of tile states.
     * More efficient than calling updateTile repeatedly.
     * @param updates Map of coordinates to new tile states
     */
    public void bulkUpdate(Map<Coord2d, TileState> updates) {
        Objects.requireNonNull(updates, "Updates map cannot be null");
        tileMap.putAll(updates);
    }
    
    /**
     * Clears all tile states (removes all tiles from the map).
     */
    public void clear() {
        tileMap.clear();
    }
    
    /**
     * Reinitializes all tiles to UNKNOWN state.
     * Useful when field boundaries change or need to rescan.
     */
    public void reset() {
        tileMap.clear();
        initializeTiles();
    }
    
    /**
     * Gets the total number of tiles being tracked.
     * @return Number of tiles in the map
     */
    public int size() {
        return tileMap.size();
    }
    
    /**
     * Gets all tiles in the map.
     * @return Unmodifiable collection of all tile states
     */
    public Collection<TileState> getAllTiles() {
        return Collections.unmodifiableCollection(tileMap.values());
    }
    
    /**
     * Gets tile state statistics for the field.
     * @return Map of state to count
     */
    public Map<TileStateEnum, Long> getStateStatistics() {
        return tileMap.values().stream()
                .collect(Collectors.groupingBy(
                        TileState::getState,
                        Collectors.counting()
                ));
    }
    
    /**
     * Checks if the tile map contains a tile at the specified coordinate.
     * @param coord Grid coordinate
     * @return true if tile exists in map
     */
    public boolean containsTile(Coord2d coord) {
        return tileMap.containsKey(coord);
    }
}
