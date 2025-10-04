package auto.farming.model;

import haven.Coord2d;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the grid-based boundaries of a farm field.
 * 
 * <p>Supports both rectangular and circular field shapes with precise
 * boundary checking and tile enumeration. Grids align with Haven's 11x11
 * tile system.</p>
 */
public class FieldGrid {
    private final FieldShape shape;
    private Coord2d origin;      // For rectangles: top-left; For circles: center
    private Coord2d dimensions;  // For rectangles: width/height; For circles: radius/radius
    
    /**
     * Private constructor. Use static factory methods instead.
     * 
     * @param shape Field shape
     * @param origin Origin point (top-left for rectangle, center for circle)
     * @param dimensions Dimensions (width/height for rectangle, radius/radius for circle)
     */
    private FieldGrid(FieldShape shape, Coord2d origin, Coord2d dimensions) {
        this.shape = shape;
        this.origin = origin;
        this.dimensions = dimensions;
    }
    
    /**
     * Creates a rectangular field grid.
     * 
     * @param minX Minimum X grid coordinate
     * @param minY Minimum Y grid coordinate
     * @param maxX Maximum X grid coordinate
     * @param maxY Maximum Y grid coordinate
     * @return FieldGrid representing the rectangle
     */
    public static FieldGrid rectangle(double minX, double minY, double maxX, double maxY) {
        Coord2d origin = new Coord2d(minX, minY);
        Coord2d dimensions = new Coord2d(maxX - minX, maxY - minY);
        return new FieldGrid(FieldShape.RECTANGLE, origin, dimensions);
    }
    
    /**
     * Creates a circular field grid.
     * 
     * @param centerX Center X grid coordinate
     * @param centerY Center Y grid coordinate
     * @param radius Radius in grid units
     * @return FieldGrid representing the circle
     */
    public static FieldGrid circle(double centerX, double centerY, double radius) {
        Coord2d center = new Coord2d(centerX, centerY);
        Coord2d radiusDim = new Coord2d(radius, radius);
        return new FieldGrid(FieldShape.CIRCLE, center, radiusDim);
    }
    
    /**
     * Checks if a point is contained within this field's boundaries.
     * 
     * @param point Grid coordinate to test
     * @return true if point is within field boundaries, false otherwise
     */
    public boolean contains(Coord2d point) {
        if (point == null) {
            return false;
        }
        
        switch (shape) {
            case RECTANGLE:
                return containsRectangle(point);
            case CIRCLE:
                return containsCircle(point);
            default:
                return false;
        }
    }
    
    /**
     * Checks if a point is within the rectangular boundary.
     */
    private boolean containsRectangle(Coord2d point) {
        double minX = origin.x;
        double minY = origin.y;
        double maxX = origin.x + dimensions.x;
        double maxY = origin.y + dimensions.y;
        
        return point.x >= minX && point.x <= maxX 
            && point.y >= minY && point.y <= maxY;
    }
    
    /**
     * Checks if a point is within the circular boundary.
     */
    private boolean containsCircle(Coord2d point) {
        double dx = point.x - origin.x;
        double dy = point.y - origin.y;
        double distanceSquared = dx * dx + dy * dy;
        double radiusSquared = dimensions.x * dimensions.x;
        
        return distanceSquared <= radiusSquared;
    }
    
    /**
     * Returns all grid tiles that are fully or partially within this field.
     * 
     * @return List of grid coordinates for each tile in the field
     */
    public List<Coord2d> getTiles() {
        List<Coord2d> tiles = new ArrayList<>();
        
        switch (shape) {
            case RECTANGLE:
                tiles = getTilesRectangle();
                break;
            case CIRCLE:
                tiles = getTilesCircle();
                break;
        }
        
        return tiles;
    }
    
    /**
     * Enumerates all tiles in a rectangular field.
     */
    private List<Coord2d> getTilesRectangle() {
        List<Coord2d> tiles = new ArrayList<>();
        
        int minX = (int) Math.floor(origin.x);
        int minY = (int) Math.floor(origin.y);
        int maxX = (int) Math.ceil(origin.x + dimensions.x);
        int maxY = (int) Math.ceil(origin.y + dimensions.y);
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                tiles.add(new Coord2d(x, y));
            }
        }
        
        return tiles;
    }
    
    /**
     * Enumerates all tiles in a circular field.
     */
    private List<Coord2d> getTilesCircle() {
        List<Coord2d> tiles = new ArrayList<>();
        
        double radius = dimensions.x;
        int minX = (int) Math.floor(origin.x - radius);
        int minY = (int) Math.floor(origin.y - radius);
        int maxX = (int) Math.ceil(origin.x + radius);
        int maxY = (int) Math.ceil(origin.y + radius);
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Coord2d tile = new Coord2d(x, y);
                if (contains(tile)) {
                    tiles.add(tile);
                }
            }
        }
        
        return tiles;
    }
    
    /**
     * Returns the center point of this field grid.
     * 
     * @return Center grid coordinates
     */
    public Coord2d getCenter() {
        switch (shape) {
            case RECTANGLE:
                return new Coord2d(
                    origin.x + dimensions.x / 2.0,
                    origin.y + dimensions.y / 2.0
                );
            case CIRCLE:
                return new Coord2d(origin.x, origin.y);
            default:
                return origin;
        }
    }
    
    /**
     * Calculates the approximate area of this field in grid units squared.
     * 
     * @return Area in grid units squared
     */
    public double getArea() {
        switch (shape) {
            case RECTANGLE:
                return dimensions.x * dimensions.y;
            case CIRCLE:
                double radius = dimensions.x;
                return Math.PI * radius * radius;
            default:
                return 0.0;
        }
    }
    
    /**
     * Returns the bounding box of this field.
     * 
     * @return Array of [minX, minY, maxX, maxY]
     */
    public double[] getBounds() {
        switch (shape) {
            case RECTANGLE:
                return new double[] {
                    origin.x,
                    origin.y,
                    origin.x + dimensions.x,
                    origin.y + dimensions.y
                };
            case CIRCLE:
                double radius = dimensions.x;
                return new double[] {
                    origin.x - radius,
                    origin.y - radius,
                    origin.x + radius,
                    origin.y + radius
                };
            default:
                return new double[] {0, 0, 0, 0};
        }
    }
    
    /**
     * @return The shape of this field grid
     */
    public FieldShape getShape() {
        return shape;
    }
    
    /**
     * @return The origin point (top-left for rectangle, center for circle)
     */
    public Coord2d getOrigin() {
        return origin;
    }
    
    /**
     * @return The dimensions (width/height for rectangle, radius/radius for circle)
     */
    public Coord2d getDimensions() {
        return dimensions;
    }
    
    /**
     * Sets the origin point.
     * 
     * @param origin New origin point
     */
    public void setOrigin(Coord2d origin) {
        this.origin = origin;
    }
    
    /**
     * Sets the dimensions.
     * 
     * @param dimensions New dimensions
     */
    public void setDimensions(Coord2d dimensions) {
        this.dimensions = dimensions;
    }
    
    @Override
    public String toString() {
        return String.format("FieldGrid[shape=%s, origin=(%.2f, %.2f), dims=(%.2f, %.2f)]",
            shape, origin.x, origin.y, dimensions.x, dimensions.y);
    }
}
