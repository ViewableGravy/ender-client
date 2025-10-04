package auto.farming.util;

import haven.Coord;
import haven.Coord2d;
import haven.MCache;

/**
 * Utility class for grid coordinate conversions and calculations.
 * 
 * <p>Haven & Hearth uses an 11x11 unit tile system. This class provides
 * utilities for converting between world coordinates and grid tile coordinates.</p>
 */
public class GridUtils {
    /**
     * Haven's tile size in world units (11x11).
     */
    public static final double TILE_SIZE = MCache.tilesz.x; // 11.0
    
    /**
     * Converts world coordinates to grid tile coordinates.
     * 
     * @param world World coordinates
     * @return Grid tile coordinates (each tile is TILE_SIZE units)
     */
    public static Coord2d worldToGrid(Coord world) {
        return new Coord2d(world.x / TILE_SIZE, world.y / TILE_SIZE);
    }
    
    /**
     * Converts world coordinates to grid tile coordinates.
     * 
     * @param world World coordinates
     * @return Grid tile coordinates (each tile is TILE_SIZE units)
     */
    public static Coord2d worldToGrid(Coord2d world) {
        return new Coord2d(world.x / TILE_SIZE, world.y / TILE_SIZE);
    }
    
    /**
     * Converts grid tile coordinates to world coordinates.
     * 
     * @param grid Grid tile coordinates
     * @return World coordinates (center of tile)
     */
    public static Coord gridToWorld(Coord2d grid) {
        return new Coord((int)(grid.x * TILE_SIZE), (int)(grid.y * TILE_SIZE));
    }
    
    /**
     * Converts grid tile coordinates to world coordinates.
     * 
     * @param grid Grid tile coordinates
     * @return World coordinates as Coord2d (center of tile)
     */
    public static Coord2d gridToWorldCoord2d(Coord2d grid) {
        return new Coord2d(grid.x * TILE_SIZE, grid.y * TILE_SIZE);
    }
    
    /**
     * Floors a coordinate to the nearest tile boundary.
     * 
     * @param world World coordinates
     * @return World coordinates aligned to tile grid
     */
    public static Coord2d floorToTile(Coord2d world) {
        Coord2d grid = worldToGrid(world);
        return new Coord2d(Math.floor(grid.x), Math.floor(grid.y));
    }
    
    /**
     * Calculates the distance between two grid coordinates.
     * 
     * @param a First grid coordinate
     * @param b Second grid coordinate
     * @return Euclidean distance in grid units
     */
    public static double distance(Coord2d a, Coord2d b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Calculates the Manhattan distance between two grid coordinates.
     * 
     * @param a First grid coordinate
     * @param b Second grid coordinate
     * @return Manhattan distance in grid units
     */
    public static double manhattanDistance(Coord2d a, Coord2d b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}
